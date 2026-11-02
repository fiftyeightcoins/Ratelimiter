package com.showchase.myRateLimiter.service.impl;

import com.showchase.myRateLimiter.pojo.model.ClientData;
import com.showchase.myRateLimiter.repository.RedisRepository;
import com.showchase.myRateLimiter.service.ClientLockService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class ClientLockServiceImpl implements ClientLockService {

    private volatile Map<String, ReentrantLock> clientsLock = Collections.emptyMap();

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public void addClient(String clientId) {
        Map<String, ReentrantLock> currMap = new HashMap<>(clientsLock);
        currMap.putIfAbsent(clientId, new ReentrantLock());
        clientsLock = Collections.unmodifiableMap(currMap);
    }

    @Override
    public void acquireLock(String clientId) {
        ReentrantLock lock = clientsLock.get(clientId);
        if (lock == null) {
            addClient(clientId);
            lock = clientsLock.get(clientId);
        }
        lock.lock();
    }

    @PostConstruct
    public void init() {
        try {
            List<ClientData> configuredClients = redisRepository.findAllClients();
            clientsLock = Collections.unmodifiableMap(configuredClients.stream()
                    .collect(Collectors.toMap(ClientData::getClientId, client -> new ReentrantLock())));
        } catch (Exception e) {
            System.err.println("Could not load clients from Redis on startup: " + e.getMessage());
        }
    }

    @Override
    public void releaseLock(String clientId) {
        ReentrantLock lock = clientsLock.get(clientId);
        if (lock == null) {
            throw new IllegalArgumentException("Unknown client: " + clientId);
        }
        lock.unlock();
    }
}

