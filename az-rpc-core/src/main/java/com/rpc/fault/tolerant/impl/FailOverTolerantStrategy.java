package com.rpc.fault.tolerant.impl;

import com.rpc.fault.tolerant.TolerantStrategy;
import com.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 转移到其他服务节点
 */
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        return null;
    }
}
