package com.showchase.myRateLimiter.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.showchase.myRateLimiter.enums.LimitType;
import com.showchase.myRateLimiter.pojo.model.ClientData;
import com.showchase.myRateLimiter.pojo.model.ClientRateLimitData;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;

import java.util.*;

@Repository
public class RedisRepository {

    private final UnifiedJedis jedis;
    private final ObjectMapper objectMapper;

    public RedisRepository(UnifiedJedis jedis, ObjectMapper objectMapper) {
        this.jedis = jedis;
        this.objectMapper = objectMapper;
    }

    // Client persistence
    public void saveClient(ClientData client) {
        jedis.sadd("clients", client.getClientId());
    }

    public List<ClientData> findAllClients() {
        Set<String> clientIds = jedis.smembers("clients");
        if (clientIds == null || clientIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<ClientData> list = new ArrayList<>();
        for (String id : clientIds) {
            list.add(new ClientData(id));
        }
        return list;
    }

    // Rate Limit Data persistence
    public void saveRateLimit(ClientRateLimitData limitData) {
        try {
            String json = objectMapper.writeValueAsString(limitData);
            String redisKey = "ratelimit:" + limitData.getClientId() + ":" + limitData.getId();
            jedis.set(redisKey, json);
            jedis.sadd("client_limits:" + limitData.getClientId(), limitData.getId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize rate limit data", e);
        }
    }

    public void saveAllRateLimits(List<ClientRateLimitData> limitDataList) {
        for (ClientRateLimitData data : limitDataList) {
            saveRateLimit(data);
        }
    }

    public List<ClientRateLimitData> findAllRateLimits() {
        Set<String> clientIds = jedis.smembers("clients");
        if (clientIds == null || clientIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<ClientRateLimitData> allLimits = new ArrayList<>();
        for (String clientId : clientIds) {
            allLimits.addAll(findLimitsByClientId(clientId));
        }
        return allLimits;
    }

    public List<ClientRateLimitData> findLimitsByClientId(String clientId) {
        Set<String> limitIds = jedis.smembers("client_limits:" + clientId);
        if (limitIds == null || limitIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<ClientRateLimitData> limits = new ArrayList<>();
        for (String limitId : limitIds) {
            String json = jedis.get("ratelimit:" + clientId + ":" + limitId);
            if (json != null) {
                try {
                    limits.add(objectMapper.readValue(json, ClientRateLimitData.class));
                } catch (JsonProcessingException e) {
                    System.err.println("Error deserializing limit id " + limitId + ": " + e.getMessage());
                }
            }
        }
        return limits;
    }

    public List<ClientRateLimitData> fetchApplicableApiLimitsInOrder(
            String clientId,
            String methodName,
            String apiName,
            LimitType defaultType,
            LimitType methodType,
            LimitType apiType
    ) {
        List<ClientRateLimitData> allLimits = findLimitsByClientId(clientId);
        List<ClientRateLimitData> applicable = new ArrayList<>();

        for (ClientRateLimitData limit : allLimits) {
            if (limit.getLimitType() == defaultType) {
                applicable.add(limit);
            } else if (limit.getLimitType() == methodType && limit.getLimitName().equalsIgnoreCase(methodName)) {
                applicable.add(limit);
            } else if (limit.getLimitType() == apiType && limit.getLimitName().equalsIgnoreCase(apiName)) {
                applicable.add(limit);
            }
        }

        // Sort by limitType ordinal (DEFAULT, METHOD, API) then by timeUnit
        applicable.sort(Comparator
                .comparing((ClientRateLimitData l) -> l.getLimitType() != null ? l.getLimitType().ordinal() : 0)
                .thenComparing(l -> l.getTimeUnit() != null ? l.getTimeUnit().ordinal() : 0));

        return applicable;
    }
}
