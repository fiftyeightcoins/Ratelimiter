package com.showchase.myRateLimiter.pojo.request;

import java.util.List;

public class ClientLimitsConfigRequest {

    private String limitType;
    private String limitName;
    private List<ClientIntervalRequestsLimit> timeIntervalLimits;

    public ClientLimitsConfigRequest() {}

    public String getLimitType() { return limitType; }
    public void setLimitType(String limitType) { this.limitType = limitType; }

    public String getLimitName() { return limitName; }
    public void setLimitName(String limitName) { this.limitName = limitName; }

    public List<ClientIntervalRequestsLimit> getTimeIntervalLimits() { return timeIntervalLimits; }
    public void setTimeIntervalLimits(List<ClientIntervalRequestsLimit> timeIntervalLimits) { this.timeIntervalLimits = timeIntervalLimits; }
}
