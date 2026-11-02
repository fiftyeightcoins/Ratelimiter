package com.showchase.myRateLimiter.pojo.response;

public class BaseResponse {

    private String message;
    private String status;

    public BaseResponse() {}

    public BaseResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
