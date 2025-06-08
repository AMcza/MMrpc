package com.rpc.server.http;

import com.rpc.model.RpcRequest;
import com.rpc.model.RpcResponse;
import com.rpc.registry.LocalRegistry;
import com.rpc.serializer.JDKSerializer;
import com.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = new JDKSerializer();

        System.out.println("收到请求");
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("请求为空");
                rpcResponse.setException(new Exception("请求为空"));
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(request, rpcResponse, serializer);
        });
    }

    void doResponse(HttpServerRequest request, RpcResponse response, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response();
        httpServerResponse.putHeader("Content-Type", "application/json;charset=utf-8");
        try {
            byte[] serializedData = serializer.serialize(response);
            httpServerResponse.putHeader("Content-Length", String.valueOf(serializedData.length));
            httpServerResponse.write(Buffer.buffer(serializedData));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.putHeader("Content-Length", "0");
            httpServerResponse.end(Buffer.buffer());
        }
    }
}