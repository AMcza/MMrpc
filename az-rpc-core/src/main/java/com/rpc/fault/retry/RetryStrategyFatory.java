package com.rpc.fault.retry;

import com.rpc.fault.retry.impl.NoRetryStrategy;
import com.rpc.spi.SpiLoader;

public class RetryStrategyFatory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试
     **/
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();


    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class,key);
    }
}


