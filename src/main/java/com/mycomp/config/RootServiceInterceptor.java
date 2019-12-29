package com.mycomp.config;

import com.mycomp.service.LimiterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Queue;

@Component
@Slf4j
public class RootServiceInterceptor implements HandlerInterceptor {

    private final LimiterService limiterService;

    @Autowired
    public RootServiceInterceptor(LimiterService limiterService) {
        this.limiterService = limiterService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        String requestURI = request.getRequestURI();
        boolean canBeProceeded = preHandleRequest(requestURI);
        if (!canBeProceeded) {
            if (logger.isDebugEnabled()) {
                logger.debug("Reject '{}', due to {}", requestURI, HttpStatus.TOO_MANY_REQUESTS);
            }
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        }
        return canBeProceeded;
    }

    private boolean preHandleRequest(String uri) {
        logger.info("Incoming request for URI resource: {}", uri);
        if (StringUtils.equals(ServiceURIConstant.CITY, uri)) {
            return preHandleCityRequest();
        } else if (StringUtils.equals(ServiceURIConstant.ROOM, uri)) {
            return preHandleRoomRequest();
        } else {
            logger.warn("Unknown URI: {}", uri);
            return true;
        }
    }

    private boolean preHandleCityRequest() {
        Queue<LocalDateTime> cityBucket = limiterService.getCityBucket();
        if (limiterService.getCityBucketLock().get()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bucket is locked, request will not be processed");
            }
            return false;
        }
        boolean addAble = cityBucket.offer(LocalDateTime.now());
        if (!addAble) {
            limiterService.lockCityBucket();
        }
        return addAble;
    }

    private boolean preHandleRoomRequest() {
        Queue<LocalDateTime> roomBucket = limiterService.getRoomBucket();
        if (limiterService.getRoomBucketLock().get()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bucket is locked, request will not be processed");
            }
            return false;
        }
        boolean addAble = roomBucket.offer(LocalDateTime.now());
        if (!addAble) {
            limiterService.lockRoomBucket();
        }
        return addAble;
    }
}
