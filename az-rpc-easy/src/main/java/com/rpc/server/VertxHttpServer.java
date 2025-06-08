package com.rpc.server;
import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        //创建Vert.x实例
        Vertx vertex= Vertx.vertx();

        //创建http服务器
        io.vertx.core.http.HttpServer server=vertex.createHttpServer();
        //绑定请求处理器
        server.requestHandler(new HttpServerHandler());


        //启动服务并监听指定端口
        server.listen(port)
                .onSuccess(httpServerAsyncResult -> {System.out.println("服务启动成功"+port);})
                .onFailure(throwable -> {System.out.println("服务启动失败"+throwable.getMessage());});

    }
}
