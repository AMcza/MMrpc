package com.rpc.server.tcp;

import com.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;

public class TcpServerHandler implements Handler<NetSocket> {


    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper=new TcpBufferHandlerWrapper(buffer -> {

        });
        netSocket.handler(bufferHandlerWrapper);
    }
}
