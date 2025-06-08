package com.example.provider;

import com.rpc.RpcApplication;
import com.rpc.registry.LocalRegistry;
import com.rpc.server.HttpServer;
import com.rpc.server.http.VertxHttpServer;
import example.common.service.UserService;

public class EasyProviderExample {

    public static void main(String[] args) {
        //RPC框架初始化
        RpcApplication.init();

        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
