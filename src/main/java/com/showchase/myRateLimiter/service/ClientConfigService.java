package com.showchase.myRateLimiter.service;

import com.showchase.myRateLimiter.pojo.request.ClientConfigRequest;
import com.showchase.myRateLimiter.pojo.response.BaseResponse;





public interface ClientConfigService {

    BaseResponse configureClient(ClientConfigRequest clientConfigRequest);

}
