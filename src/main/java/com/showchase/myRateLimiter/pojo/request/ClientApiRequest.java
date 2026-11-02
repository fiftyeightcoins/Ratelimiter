package com.showchase.myRateLimiter.pojo.request;

public class ClientApiRequest {

    private String clientId;
    private String methodname;
    private String apiName;

    public ClientApiRequest() {}

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getMethodname() { return methodname; }
    public void setMethodname(String methodname) { this.methodname = methodname; }

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
}
