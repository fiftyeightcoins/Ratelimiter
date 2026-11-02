package com.showchase.myRateLimiter.pojo.model;

import com.showchase.myRateLimiter.enums.LimitType;
import com.showchase.myRateLimiter.enums.TimeUnit;

public class ClientRateLimitData {

    private String id;
    private String clientId;
    private String limitName;
    private LimitType limitType;
    private TimeUnit timeUnit;
    private long maxPermits;
    private long maxLimits;
    private long availablePermits;
    private long lastRequestTimeStamp;

    public ClientRateLimitData() {}

    public ClientRateLimitData(String id, String clientId, String limitName, LimitType limitType,
                               TimeUnit timeUnit, long maxPermits, long maxLimits,
                               long availablePermits, long lastRequestTimeStamp) {
        this.id = id;
        this.clientId = clientId;
        this.limitName = limitName;
        this.limitType = limitType;
        this.timeUnit = timeUnit;
        this.maxPermits = maxPermits;
        this.maxLimits = maxLimits;
        this.availablePermits = availablePermits;
        this.lastRequestTimeStamp = lastRequestTimeStamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getLimitName() { return limitName; }
    public void setLimitName(String limitName) { this.limitName = limitName; }

    public LimitType getLimitType() { return limitType; }
    public void setLimitType(LimitType limitType) { this.limitType = limitType; }

    public TimeUnit getTimeUnit() { return timeUnit; }
    public void setTimeUnit(TimeUnit timeUnit) { this.timeUnit = timeUnit; }

    public long getMaxPermits() { return maxPermits; }
    public void setMaxPermits(long maxPermits) { this.maxPermits = maxPermits; }

    public long getMaxLimits() { return maxLimits; }
    public void setMaxLimits(long maxLimits) { this.maxLimits = maxLimits; }

    public long getAvailablePermits() { return availablePermits; }
    public void setAvailablePermits(long availablePermits) { this.availablePermits = availablePermits; }

    public long getLastRequestTimeStamp() { return lastRequestTimeStamp; }
    public void setLastRequestTimeStamp(long lastRequestTimeStamp) { this.lastRequestTimeStamp = lastRequestTimeStamp; }
}
