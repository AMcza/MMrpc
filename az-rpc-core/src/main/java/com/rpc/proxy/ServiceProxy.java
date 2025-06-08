package com.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.rpc.RpcApplication;
import com.rpc.config.RpcConfig;
import com.rpc.constant.RpcConstant;
import com.rpc.fault.retry.RetryStrategy;
import com.rpc.fault.retry.RetryStrategyFatory;
import com.rpc.fault.tolerant.TolerantStrategy;
import com.rpc.fault.tolerant.TolerantStrategyFactory;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.LoadBalancerFactory;
import com.rpc.model.RpcRequest;
import com.rpc.model.RpcResponse;
import com.rpc.model.ServiceMetaInfo;
import com.rpc.registry.Registry;
import com.rpc.registry.RegistryFactory;
import com.rpc.serializer.JDKSerializer;
import com.rpc.serializer.Serializer;
import com.rpc.serializer.SerializerFactory;
import com.rpc.server.tcp.VertTcpClient;
import com.rpc.server.tcp.VertxTcpClient;
import com.rpc.server.tcp.VertxTcpServerPlus;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 服务代理(jdk动态代理)
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //指定序列化器
            final Serializer serializer= SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());


            //构造请求
            String serviceName=method.getDeclaringClass().getName();
            RpcRequest rpcRequest=RpcRequest.builder()
                    .serviceName(serviceName)
                    .methodName(method.getName())
                    .parameterTypes(method.getParameterTypes())
                    .args(args)
                    .build();



            //从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig=RpcApplication.getRpcConfig();
            Registry registry= RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList=registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("未找到服务提供者");
            }
//            ServiceMetaInfo selectServiceMetaInfo=serviceMetaInfoList.get(0);
//
//
////            //发送请求
////            try(HttpResponse httpResponse= HttpRequest.post(selectServiceMetaInfo.getServiceAddress())
////                    .body(bytes)
////                    .execute()){
////                byte[] result=httpResponse.bodyBytes();
////                //反序列化
////                RpcResponse rpcResponse=serializer.deserialize(result,RpcResponse.class);
////                return rpcResponse.getData();
////            }
            //负载均衡
            LoadBalancer loadBalancer= LoadBalancerFactory.getLoadBalancer(rpcConfig.getLoadBalancer());
            //将调用方法名
            Map<String,Object> requestParams=new HashMap<>();
            requestParams.put("methodName",method.getName());
            ServiceMetaInfo selectServiceMetaInfo=loadBalancer.select(requestParams,serviceMetaInfoList);
            //rpc请求
            //重试机制
            RpcResponse rpcResponse;
            try{
                RetryStrategy retryStrategy= RetryStrategyFatory.getInstance(rpcConfig.getRetryStrategy());

                rpcResponse= retryStrategy.doRetry(()->
                        VertxTcpClient.doRequest(rpcRequest, selectServiceMetaInfo)
                );
            }catch (IOException e) {
                //容错机制
                TolerantStrategy tolerantStrategy= TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse=tolerantStrategy.doTolerant(null,e);
                throw new RuntimeException("调用失败");
            }
            return rpcResponse.getData();
    }
}
