package com.rpc.server.tcp;

import io.vertx.core.Vertx;

public class VertTcpClient {

    public void start(){
        Vertx vertx=Vertx.vertx();


        vertx.createNetClient().connect(8080,"localhost",result->{
            if(result.succeeded()){
                System.out.println("连接成功");
                io.vertx.core.net.NetSocket socket=result.result();
                //发送数据
                socket.write("Hello,server!");
            }else{
                System.out.println("连接失败");
            }
        });
    }

    public static void main(String[] args) {
        new VertTcpClient().start();
    }
}
