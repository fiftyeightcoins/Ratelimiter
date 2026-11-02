package com.showchase.myRateLimiter.pojo.request;

import java.util.List;

public class ClientConfigRequest {

    private String clientId;
    private List<ClientLimitsConfigRequest> limits;

    public ClientConfigRequest() {}

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public List<ClientLimitsConfigRequest> getLimits() { return limits; }
    public void setLimits(List<ClientLimitsConfigRequest> limits) { this.limits = limits; }
}
