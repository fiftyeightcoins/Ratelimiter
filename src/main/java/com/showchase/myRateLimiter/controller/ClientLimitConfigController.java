package com.showchase.myRateLimiter.controller;

import com.showchase.myRateLimiter.pojo.request.ClientApiRequest;
import com.showchase.myRateLimiter.pojo.request.ClientConfigRequest;
import com.showchase.myRateLimiter.pojo.response.BaseResponse;
import com.showchase.myRateLimiter.repository.RedisRepository;
import com.showchase.myRateLimiter.service.ClientConfigService;
import com.showchase.myRateLimiter.service.ClientRateLimitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/throttling")
public class ClientLimitConfigController {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private ClientConfigService clientConfigService;

    @Autowired
    private ClientRateLimitingService clientRateLimitingService;

    @PostMapping("/configure-client")
    public BaseResponse configureClient(@RequestBody ClientConfigRequest clientConfigRequest) {
        return clientConfigService.configureClient(clientConfigRequest);
    }

    @PostMapping("/verify-api-limit")
    public BaseResponse verifyApiLimit(@RequestBody ClientApiRequest clientApiRequest) {
        return clientRateLimitingService.verifyApiLimit(clientApiRequest);
    }

    @GetMapping("/configured-limits")
    public List<?> getConfiguredLimits() {
        return redisRepository.findAllRateLimits();
    }

    @GetMapping("/test")
    public String test() {
        return "works";
    }
}
