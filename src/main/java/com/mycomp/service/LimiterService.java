package com.mycomp.service;

import com.mycomp.utils.NumberConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class LimiterService {

    private final Timer unlockerTimer = new Timer("Unlokcer timer");
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private Queue<LocalDateTime> cityBucket;
    private AtomicBoolean cityBucketLock = new AtomicBoolean(false);
    private Queue<LocalDateTime> roomBucket;
    private AtomicBoolean roomBucketLock = new AtomicBoolean(false);

    @Value("${endpoint.city.requests.limit.every.5.seconds:-1}")
    private int cityRequestLimit;
    @Value("${endpoint.room.requests.limit.every.10.seconds:-1}")
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
        int clearRequestInEveryMS;
        if (cityRequestLimit > 0) {
            clearRequestInEveryMS = NumberConversionUtils.secondsToMilliseconds(5) / cityRequestLimit;
            logger.info("Init city request limit to {} reqs / 5 seconds, will clear 1 req / {} milliseconds", cityRequestLimit, clearRequestInEveryMS);
            cityBucket = new ArrayBlockingQueue<>(cityRequestLimit, true);
            scheduledExecutor.scheduleWithFixedDelay(this::performCityBucketScheduler, 0, clearRequestInEveryMS, TimeUnit.MILLISECONDS);
        } else {
            clearRequestInEveryMS = NumberConversionUtils.secondsToMilliseconds(10) / defaultRequestLimit;
            logger.info("City request limit falls back to {} reqs / 10 seconds, will clear 1 req / {} milliseconds", defaultRequestLimit, clearRequestInEveryMS);
            cityBucket = new ArrayBlockingQueue<>(defaultRequestLimit, true);
            scheduledExecutor.scheduleWithFixedDelay(this::performCityBucketScheduler, 0, 10, TimeUnit.MILLISECONDS);
        }
    }

    private void initRoomScheduler() {
        int clearRequestInEveryMS;
        if (roomRequestLimit > 0) {
            clearRequestInEveryMS = NumberConversionUtils.secondsToMilliseconds(10) / roomRequestLimit;
            logger.info("Init room request limit to {} reqs / 10 seconds, will clear 1 req / {} milliseconds", roomRequestLimit, clearRequestInEveryMS);
            roomBucket = new ArrayBlockingQueue<>(roomRequestLimit);
        } else {
            clearRequestInEveryMS = NumberConversionUtils.secondsToMilliseconds(10) / defaultRequestLimit;
            logger.info("Room request limit falls back to {} reqs / 10 seconds will clear 1 req / {} milliseconds", defaultRequestLimit, clearRequestInEveryMS);
            roomBucket = new ArrayBlockingQueue<>(defaultRequestLimit);
            scheduledExecutor.scheduleWithFixedDelay(this::performRoomBucketScheduler, 0, clearRequestInEveryMS, TimeUnit.MILLISECONDS);
        }
    }

    public void performCityBucketScheduler() {
        if (logger.isTraceEnabled()) {
            logger.trace("Polling one request from city bucket...");
        }
        cityBucket.poll();
    }

    public void performRoomBucketScheduler() {
        if (logger.isTraceEnabled()) {
            logger.trace("Polling one request from room bucket...");
        }
        roomBucket.poll();
    }

    public AtomicBoolean getCityBucketLock() {
        return cityBucketLock;
    }

    public AtomicBoolean getRoomBucketLock() {
        return roomBucketLock;
    }

    public void lockCityBucket() {
        logger.info("Locking city bucket...");
        cityBucketLock.set(true);
        TimerTask task = new TimerTask() {
            public void run() {
                logger.info("Unlocking city bucket...");
                cityBucketLock.set(false);
            }
        };
        unlockerTimer.schedule(task, NumberConversionUtils.secondsToMilliseconds(5));
    }

    public void lockRoomBucket() {
        logger.info("Locking room bucket...");
        roomBucketLock.set(true);
        TimerTask task = new TimerTask() {
            public void run() {
                logger.info("Unlocking room bucket...");
                roomBucketLock.set(false);
            }
        };
        unlockerTimer.schedule(task, NumberConversionUtils.secondsToMilliseconds(5));
    }
}
