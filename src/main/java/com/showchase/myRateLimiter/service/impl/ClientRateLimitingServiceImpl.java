package com.showchase.myRateLimiter.service.impl;

import com.showchase.myRateLimiter.enums.LimitType;
import com.showchase.myRateLimiter.pojo.model.ClientRateLimitData;
import com.showchase.myRateLimiter.pojo.request.ClientApiRequest;
import com.showchase.myRateLimiter.pojo.response.BaseResponse;
import com.showchase.myRateLimiter.repository.RedisRepository;
import com.showchase.myRateLimiter.service.ClientLockService;
import com.showchase.myRateLimiter.service.ClientRateLimitingService;
import com.showchase.myRateLimiter.util.TimeUnitConversionUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ClientRateLimitingServiceImpl implements ClientRateLimitingService {

    private final ClientLockService clientLockService;
    private final RedisRepository redisRepository;

    public ClientRateLimitingServiceImpl(
            ClientLockService clientLockService,
            RedisRepository redisRepository) {
        this.clientLockService = clientLockService;
        this.redisRepository = redisRepository;
    }

    @Override
    public BaseResponse verifyApiLimit(ClientApiRequest clientApiRequest) {
        try {
            clientLockService.acquireLock(clientApiRequest.getClientId());
            long currentTimeStamp = System.currentTimeMillis();

            List<ClientRateLimitData> applicableLimits =
                    redisRepository.fetchApplicableApiLimitsInOrder(
                            clientApiRequest.getClientId(),
                            clientApiRequest.getMethodname(),
                            clientApiRequest.getApiName(),
                            LimitType.DEFAULT,
                            LimitType.METHOD,
                            LimitType.API
                    );

            if (CollectionUtils.isEmpty(applicableLimits)) {
                System.out.println("no limits applied");
                return new BaseResponse("nolimitapplicable", "SUCCESS");
            }

            for (ClientRateLimitData clientRateLimitData : applicableLimits) {
                long elapsedtime = currentTimeStamp - clientRateLimitData.getLastRequestTimeStamp();
                double elapsedtimeUnits = TimeUnitConversionUtil.convert(elapsedtime, clientRateLimitData.getTimeUnit());

                long updatedAvailablePermits = (long) Math.min(
                        clientRateLimitData.getAvailablePermits() + elapsedtimeUnits * clientRateLimitData.getMaxPermits(),
                        clientRateLimitData.getMaxPermits()
                );

                if (updatedAvailablePermits < 1) {
                    System.out.println("API rate limit breached for ClientData-Id: " + clientRateLimitData.getClientId()
                            + ", availablePermits: " + clientRateLimitData.getAvailablePermits()
                            + ", maxPermits: " + clientRateLimitData.getMaxPermits()
                            + ", limitType: " + clientRateLimitData.getLimitType()
                            + ", timeUnit: " + clientRateLimitData.getTimeUnit());

                    return new BaseResponse(
                            "Limit: " + clientRateLimitData.getLimitName()
                                    + ", limitType: " + clientRateLimitData.getLimitType()
                                    + ", timeUnit: " + clientRateLimitData.getTimeUnit()
                                    + " breached for the client",
                            "FAILURE"
                    );
                }

                System.out.println("API rate limit passed for ClientData-Id: " + clientRateLimitData.getClientId()
                        + ", availablePermits: " + clientRateLimitData.getAvailablePermits()
                        + ", maxPermits: " + clientRateLimitData.getMaxPermits()
                        + ", limitType: " + clientRateLimitData.getLimitType()
                        + ", timeUnit: " + clientRateLimitData.getTimeUnit());

                updatedAvailablePermits--;
                clientRateLimitData.setAvailablePermits(updatedAvailablePermits);
                clientRateLimitData.setLastRequestTimeStamp(currentTimeStamp);
            }

            redisRepository.saveAllRateLimits(applicableLimits);
            return new BaseResponse("success", "Success");

        } finally {
            clientLockService.releaseLock(clientApiRequest.getClientId());
        }
    }
}
