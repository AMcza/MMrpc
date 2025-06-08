package com.rpc.config;

import com.rpc.constant.LoadBalancerKeys;
import com.rpc.constant.RetryStrategyKeys;
import com.rpc.constant.TolerantStrategyKeys;
import com.rpc.fault.tolerant.TolerantStrategy;
import com.rpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name="az-rpc";
    /**
     * 版本号
     */
    private String version="1.0";
    /**
     * 服务器主机号
     */
    private String serverHost="localhost";
    /**
     * 服务器端口号
     */
    private Integer serverPort=8080;
    /**
     * 模拟调用
     */
    private boolean mock=false;
    /**
     * 序列化方式(默认为JDK)
     */
    private String serializer= SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig=new RegistryConfig();
    /**
     * 负载均衡器
     */
    private String loadBalancer= LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy= RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy= TolerantStrategyKeys.FAIL_FAST;
}
