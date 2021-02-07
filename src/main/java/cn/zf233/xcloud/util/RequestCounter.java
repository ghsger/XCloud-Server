package cn.zf233.xcloud.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zf233 on 2021/1/30
 */
public class RequestCounter {

    private static final RequestCounter REQUEST_COUNTER = new RequestCounter();

    private RequestCounter() {}

    public static RequestCounter getInstance() {
        return REQUEST_COUNTER;
    }

    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);

    public void newRequestReceive() {
        requestCount.incrementAndGet();
    }

    public void requestSuccess() {
        successCount.incrementAndGet();
    }

    public void requestFailure() {
        failureCount.incrementAndGet();
    }

    public Long getRequestCount() {
        return requestCount.get();
    }

    public Long getRequestSuccessCount() {
        return successCount.get();
    }

    public Long getRequestFailureCount() {
        return failureCount.get();
    }
}
