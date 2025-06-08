package com.rpc.loadbalancer;

import com.rpc.loadbalancer.impl.RoundRobinLoadBalancer;
import com.rpc.spi.SpiLoader;

public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER=new RoundRobinLoadBalancer();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static LoadBalancer getLoadBalancer(String key){
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }
}
