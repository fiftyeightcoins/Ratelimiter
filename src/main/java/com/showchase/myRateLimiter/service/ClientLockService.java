package com.showchase.myRateLimiter.service;

public interface ClientLockService {

    public void addClient(String clientId);
    public void acquireLock(String clientId);
    public void releaseLock(String clientId);

}
