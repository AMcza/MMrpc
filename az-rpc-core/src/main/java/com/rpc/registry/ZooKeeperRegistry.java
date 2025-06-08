package com.rpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.rpc.config.RegistryConfig;
import com.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ZooKeeperRegistry implements Registry{

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 本机注册的节点
     */
    private final Set<String> localRegisterNodeKeySet=new HashSet<>();
    /**
     * 注册中心缓存
     */
    private final RegistryServiceCache registryServiceCache=new RegistryServiceCache();

    /**
     * 正在监听的key集合
     */
    private final Set<String> watchingKeySet=new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH="/rpc/zk";

    @Override
    public void init(RegistryConfig registryConfig) {
        //构建client实例
        client= CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()),3))
                .build();
        //构建 serviceDiscovery 实例
        serviceDiscovery= ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();
        try{
            //启动client和serviceDiscovery
            client.start();
            serviceDiscovery.start();
        }catch (Exception e){
            throw new RuntimeException("zookeeper registry init failed",e);
        }
    }



    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //注册到zk里
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        //节点信息添加到本地缓存
        String registryKey=ZK_ROOT_PATH+"/"+serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try{
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        //从本地移除缓存
        String registryKey=ZK_ROOT_PATH+"/"+serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registryKey);
    }

    /**
     * 服务发现
     * @param serviceKey
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> cachedServiceMetaInfoList=registryServiceCache.readCache();
        if(cachedServiceMetaInfoList!=null){
            return cachedServiceMetaInfoList;
        }
        try{
            //查询服务信息
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList=serviceDiscovery.queryForInstances(serviceKey);
            //解析服务
            List<ServiceMetaInfo> serviceMetaInfoList=serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());
            //写入缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败");
        }
    }

    @Override
    public void heartBeat() {
        //不需要心跳机制,建立临时节点,如果服务器故障,临时节点直接丢失
    }

    /**
     * 监听消费者
     * @param serviceNodeKey
     */
    @Override
    public void watch(String serviceNodeKey) {
        String watchKey=ZK_ROOT_PATH+"/"+serviceNodeKey;
        boolean newWatch= watchingKeySet.add(watchKey);
        if(newWatch){
            CuratorCache curatorCache=CuratorCache.build(client,watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                CuratorCacheListener
                        .builder()
                        .forDeletes(childData -> registryServiceCache.clearCache())
                        .forChanges(((oldNode,node)-> registryServiceCache.clearCache()))
                        .build()
            );
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        for(String key:localRegisterNodeKeySet){
            try{
                client.delete().guaranteed().forPath(key);
            }catch (Exception e){
                throw new RuntimeException(key+"节点删除失败",e);
            }
        }
        if(client!=null){
            client.close();
        }
    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo){
        String serviceAddress=serviceMetaInfo.getServiceHost()+":"+serviceMetaInfo.getServicePort();
        try{
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
