package com.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.rpc.RpcApplication;
import com.rpc.config.RpcConfig;
import com.rpc.constant.ProtocolConstant;
import com.rpc.constant.RpcConstant;
import com.rpc.enums.ProtocolMessageSerializerEnum;
import com.rpc.enums.ProtocolMessageTypeEnum;
import com.rpc.model.RpcRequest;
import com.rpc.model.RpcResponse;
import com.rpc.model.ServiceMetaInfo;
import com.rpc.protocol.ProtocolMessage;
import com.rpc.protocol.ProtocolMessageDecoder;
import com.rpc.protocol.ProtocolMessageEncoder;
import com.rpc.registry.Registry;
import com.rpc.registry.factory.RegistryFactory;
import com.rpc.serializer.Serializer;
import com.rpc.serializer.factory.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class  ServiceProxyTcp implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        final Serializer serializer= SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        String serviceName=method.getDeclaringClass().getName();
        RpcRequest rpcRequest=RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try{
            //序列化
            byte[] bodyBytes=serializer.serialize(rpcRequest);
            //从注册中心获取服务器提供者请求地址
            RpcConfig rpcConfig=RpcApplication.getRpcConfig();
            Registry registry= RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList=registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }
            ServiceMetaInfo SelectedserviceMetaInfo=serviceMetaInfoList.get(0);
            //发送TCP请求****
            Vertx vertx=Vertx.vertx();
            NetClient netClient=vertx.createNetClient();
            CompletableFuture<RpcResponse> responseFuture=new CompletableFuture<>();
            netClient.connect(SelectedserviceMetaInfo.getServicePort(), SelectedserviceMetaInfo.getServiceHost(),
                    asyncResult->{
                        if(asyncResult.succeeded()){
                            System.out.println("Connected to TCP server");
                            io.vertx.core.net.NetSocket socket=asyncResult.result();
                            //发送数据
                            //构造消息
                            ProtocolMessage<RpcRequest> protocolMessage=new ProtocolMessage<>();
                            ProtocolMessage.Header header=new ProtocolMessage.Header();
                            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValues(RpcApplication.getRpcConfig().getSerializer()).getKey());
                            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                            header.setRequestId(IdUtil.getSnowflakeNextId());
                            protocolMessage.setHeader(header);
                            protocolMessage.setBody(rpcRequest);

                            //编码请求
                            try{
                                Buffer encodeBuffer= ProtocolMessageEncoder.encode(protocolMessage);
                                socket.write(encodeBuffer);
                            }catch (IOException e){
                                throw new RuntimeException("协议消息编码错误");
                            }

                            //接受响应
                            socket.handler(buffer->{
                                try{
                                    ProtocolMessage<RpcResponse> responseMessage= (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(responseMessage.getBody());
                                }catch (IOException e){
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            });
                        }else{
                            System.out.println("Failed to connect to TCP server");
                        }
            });
            RpcResponse rpcResponse=responseFuture.get();
            //关闭连接
            netClient.close();
            return rpcResponse.getData();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
