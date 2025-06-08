package com.rpc.proxy.factory;

import com.rpc.RpcApplication;
import com.rpc.proxy.MockServiceProxy;
import com.rpc.proxy.ServiceProxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getServiceProxy(Class<?> clazz){
        if(RpcApplication.getRpcConfig().isMock()){
            return (T) getMockProxy(clazz);
        }
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new ServiceProxy());
    }

    private static <T> T getMockProxy(Class<T> serviceClass){
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy());
    }
}
