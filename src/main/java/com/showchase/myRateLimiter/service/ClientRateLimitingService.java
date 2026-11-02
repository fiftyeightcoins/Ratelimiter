package com.showchase.myRateLimiter.service;

import com.showchase.myRateLimiter.pojo.request.ClientApiRequest;
import com.showchase.myRateLimiter.pojo.response.BaseResponse;

public interface ClientRateLimitingService {

    public BaseResponse verifyApiLimit(ClientApiRequest clientApiRequest);

}
