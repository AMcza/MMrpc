package com.rpc.server.tcp;

import com.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServerPlus implements HttpServer {

    @Override
    public void doStart(int port) {
        //创建Vertx实例
        Vertx vertx=Vertx.vertx();

        //创建tcp服务器
        NetServer server=vertx.createNetServer();

        //处理请求
        server.connectHandler(socket->{
           //构造parser
            RecordParser parser=RecordParser.newFixed(8);
            parser.setOutput(new Handler<Buffer>() {
                //初始化
                int size=-1;
                //一次完整的读取(头+体)
                Buffer resultBuffer=Buffer.buffer();


                @Override
                public void handle(Buffer buffer) {
                   if(size==-1){
                       //读取消息体长度
                       size=buffer.getInt(4);
                       parser.fixedSizeMode(size);
                       //写入头信息到结果
                       resultBuffer.appendBuffer(buffer);
                   }else{
                       //写入体信息到结果
                       resultBuffer.appendBuffer(buffer);
                       System.out.println(resultBuffer.toString());
                       //重置一轮
                       parser.fixedSizeMode(8);
                       size=-1;
                       resultBuffer=Buffer.buffer();
                   }

                }
            });
            socket.handler(parser);
        });
        //启动TCP服务器并监听端口
        server.listen(port,result->{
           if(result.succeeded()){
               log.info("tcp server start success,port:{}",port);
           }else{
               log.info("tcp server start failed,port:{},error:{}",port,result.cause().getMessage());
           }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServerPlus().doStart(8080);
    }
}
