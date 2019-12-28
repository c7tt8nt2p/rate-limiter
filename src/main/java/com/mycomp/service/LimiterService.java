package com.mycomp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LimiterService {

    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private Queue<LocalDateTime> cityBucket;
    private Queue<LocalDateTime> roomBucket;

    @Value("${endpoint.city.requests.limit.every.5.seconds}")
    private int cityRequestLimit;
    @Value("${endpoint.room.requests.limit.every.10.seconds}")
    private int roomRequestLimit;
    @Value("${endpoint.any.requests.limit.per.10.seconds:50}")
    private int defaultRequestLimit;

    @PostConstruct
    public void init() {
        initCityScheduler();
        initRoomScheduler();
    }

    public Queue<LocalDateTime> getCityBucket() {
        return cityBucket;
    }

    public Queue<LocalDateTime> getRoomBucket() {
        return roomBucket;
    }

    private void initCityScheduler() {
        if (cityRequestLimit > 0) {
            logger.info("Init city request limit to {} reqs / 5 seconds", cityRequestLimit);
            cityBucket = new ArrayBlockingQueue<>(cityRequestLimit);
            scheduledExecutor.scheduleWithFixedDelay(this::performCityBucketScheduler, 0, 5, TimeUnit.SECONDS);
        } else {
            logger.info("City request limit falls back to {} reqs / 10 seconds", defaultRequestLimit);
            cityBucket = new ArrayBlockingQueue<>(defaultRequestLimit);
            scheduledExecutor.scheduleWithFixedDelay(this::performCityBucketScheduler, 0, 10, TimeUnit.SECONDS);
        }
    }

    private void initRoomScheduler() {
        if (roomRequestLimit > 0) {
            logger.info("Init room request limit to {} reqs / 10 seconds", roomRequestLimit);
            roomBucket = new ArrayBlockingQueue<>(roomRequestLimit);
        } else {
            logger.info("Room request limit falls back to {} reqs / 10 seconds", defaultRequestLimit);
            roomBucket = new ArrayBlockingQueue<>(defaultRequestLimit);
        }
        scheduledExecutor.scheduleWithFixedDelay(this::performRoomBucketScheduler, 0, 10, TimeUnit.SECONDS);
    }

    public void performCityBucketScheduler() {
        if (logger.isDebugEnabled()) {
            logger.debug("Clearing city request queue...");
        }
        cityBucket.clear();
    }

    public void performRoomBucketScheduler() {
        if (logger.isDebugEnabled()) {
            logger.debug("Clearing room request queue...");
        }
        roomBucket.clear();
    }

}
