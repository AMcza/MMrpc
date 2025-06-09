package com.rpc.bootstrap;

import com.rpc.RpcApplication;
import com.rpc.config.RegistryConfig;
import com.rpc.config.RpcConfig;
import com.rpc.model.ServiceMetaInfo;
import com.rpc.model.ServiceRegisterInfo;
import com.rpc.registry.LocalRegistry;
import com.rpc.registry.Registry;
import com.rpc.registry.RegistryFactory;
import com.rpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootstrap {
    /**
     * 初始化
     * @param serviceRegisterInfoList
     */
    public static void init(List<ServiceRegisterInfo> serviceRegisterInfoList){
        //RPC框架初始化
        RpcApplication.init();
        //全局配置
        final RpcConfig rpcConfig=RpcApplication.getRpcConfig();

        //注册服务
        for(ServiceRegisterInfo<?> serviceRegisterInfo:serviceRegisterInfoList){
            String serviceName=serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
            RegistryConfig registryConfig=rpcConfig.getRegistryConfig();
            Registry registry= RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try{
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException("注册失败");
            }
        }
        //启动服务器
        VertxTcpServer vertxTcpServer=new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
