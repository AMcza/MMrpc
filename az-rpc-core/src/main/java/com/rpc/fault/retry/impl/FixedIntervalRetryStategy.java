package com.rpc.fault.retry.impl;

import com.github.rholder.retry.*;
import com.rpc.fault.retry.RetryStrategy;
import com.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
@Slf4j
public class FixedIntervalRetryStategy implements RetryStrategy {

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer= RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                 .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                        .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener(){
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                            log.info("重试次数:{}",attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
