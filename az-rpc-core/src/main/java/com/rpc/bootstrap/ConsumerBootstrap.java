package com.rpc.bootstrap;

import com.rpc.RpcApplication;

public class ConsumerBootstrap {
    /**
     * 初始化
     */
    public static void init(){
        //RPC框架初始化(配置和注册中心)
        RpcApplication.init();
    }
}
