package com.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
@Slf4j
public class MockServiceProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据方法的返回值类型,生成特定的默认对象
        Class<?> methodReturnType=method.getReturnType();
        log.info("mock invoke:{}",method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> type){
        //判断是否为基本类型
        if(type.isPrimitive()){
            if(type==int.class){
                return 0;
            }else if(type==boolean.class){
                return false;
            }else if(type==short.class){
                return (short)0;
            }else if(type==long.class){
                return 0L;
            }
        }
        //对象类型
        return null;
    }
}
