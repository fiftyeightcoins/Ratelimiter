package com.showchase.myRateLimiter.pojo.model;

public class ClientData {

    private String clientId;

    public ClientData() {}

    public ClientData(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
}
