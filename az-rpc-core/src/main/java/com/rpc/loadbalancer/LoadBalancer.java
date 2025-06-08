package com.rpc.loadbalancer;

import com.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoadBalancer {
    /**
     * 选择服务调用
     * @param requestParams
     * @param serviceMetaInfoList
     * @return
     */
    ServiceMetaInfo select(Map<String, Object>requestParams, List<ServiceMetaInfo> serviceMetaInfoList);

}
