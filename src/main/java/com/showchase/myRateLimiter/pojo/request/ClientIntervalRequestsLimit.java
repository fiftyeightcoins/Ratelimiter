package com.showchase.myRateLimiter.pojo.request;

import com.showchase.myRateLimiter.enums.TimeUnit;

public class ClientIntervalRequestsLimit {

    private TimeUnit timeUnit;
    private long maxRequests;

    public ClientIntervalRequestsLimit() {}

    public TimeUnit getTimeUnit() { return timeUnit; }
    public void setTimeUnit(TimeUnit timeUnit) { this.timeUnit = timeUnit; }

    public long getMaxRequests() { return maxRequests; }
    public void setMaxRequests(long maxRequests) { this.maxRequests = maxRequests; }
}
