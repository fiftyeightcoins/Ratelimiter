package com.showchase.myRateLimiter.service.impl;

import com.showchase.myRateLimiter.enums.LimitType;
import com.showchase.myRateLimiter.pojo.model.ClientData;
import com.showchase.myRateLimiter.pojo.model.ClientRateLimitData;
import com.showchase.myRateLimiter.pojo.request.ClientConfigRequest;
import com.showchase.myRateLimiter.pojo.request.ClientIntervalRequestsLimit;
import com.showchase.myRateLimiter.pojo.request.ClientLimitsConfigRequest;
import com.showchase.myRateLimiter.pojo.response.BaseResponse;
import com.showchase.myRateLimiter.repository.RedisRepository;
import com.showchase.myRateLimiter.service.ClientConfigService;
import com.showchase.myRateLimiter.service.ClientLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClientConfigServiceImpl implements ClientConfigService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private ClientLockService clientLockService;

    @Override
    public BaseResponse configureClient(ClientConfigRequest request) {
        String clientId = request.getClientId();

        // 1. Save client record to Redis
        redisRepository.saveClient(new ClientData(clientId));

        // 2. Register lock in memory
        clientLockService.addClient(clientId);

        // 3. Save rate limit configurations to Redis
        long now = System.currentTimeMillis();
        List<ClientRateLimitData> limitRecords = new ArrayList<>();

        if (request.getLimits() != null) {
            for (ClientLimitsConfigRequest limitConfig : request.getLimits()) {
                LimitType limitType = LimitType.valueOf(limitConfig.getLimitType());
                String limitName = limitConfig.getLimitName();

                if (limitConfig.getTimeIntervalLimits() != null) {
                    for (ClientIntervalRequestsLimit interval : limitConfig.getTimeIntervalLimits()) {
                        ClientRateLimitData data = new ClientRateLimitData();
                        data.setId(UUID.randomUUID().toString());
                        data.setClientId(clientId);
                        data.setLimitName(limitName);
                        data.setLimitType(limitType);
                        data.setTimeUnit(interval.getTimeUnit());
                        data.setMaxPermits(interval.getMaxRequests());
                        data.setMaxLimits(interval.getMaxRequests());
                        data.setAvailablePermits(interval.getMaxRequests());
                        data.setLastRequestTimeStamp(now);

                        limitRecords.add(data);
                    }
                }
            }
        }

        redisRepository.saveAllRateLimits(limitRecords);
        return new BaseResponse("Client configured successfully", "SUCCESS");
    }
}

