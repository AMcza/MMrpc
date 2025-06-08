package com.rpc.server.tcp;

import com.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * 处理二进制流
 */
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData){

        return "hello,client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        //创建Vertx实例
        Vertx vertx = Vertx.vertx();
        //创建TCP服务器
        NetServer server=vertx.createNetServer();

        //处理请求
        server.connectHandler(socket->{
            //处理连接
            socket.handler(buffer->{
                byte[] requestData = buffer.getBytes();

                byte[] responseData = handleRequest(requestData);

                //发送响应
                socket.write(Buffer.buffer(responseData));
            });
        });
        //启动TCP服务器监听指定端口
        server.listen(port,result->{
            if(result.succeeded()){
                System.out.println("TCP服务器启动成功");
            }else{
                System.out.println("TCP服务器启动失败");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8080);
    }
}
