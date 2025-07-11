package com.rpc.server.tcp;

import com.rpc.enums.ProtocolMessageTypeEnum;
import com.rpc.model.RpcRequest;
import com.rpc.model.RpcResponse;
import com.rpc.protocol.ProtocolMessage;
import com.rpc.protocol.ProtocolMessageDecoder;
import com.rpc.protocol.ProtocolMessageEncoder;
import com.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理(服务提供者)
 */
public class TcpServerHandler2 implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        //处理连接
        netSocket.handler(buffer->{
           //接受请求,解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try{
                protocolMessage=(ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            }catch (IOException e){
                throw new RuntimeException("协议消息解码失败");
            }
            RpcRequest rpcRequest=protocolMessage.getBody();

            //处理请求
            //构造响应结果对象
            RpcResponse rpcResponse=new RpcResponse();
            try{
                //获取要调用的服务实现类,通过反射调用
                Class<?> implClass= LocalRegistry.get(rpcRequest.getServiceName());
                Method method=implClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                Object result=method.invoke(implClass.newInstance(),rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }


            //发送响应
            ProtocolMessage.Header header=new ProtocolMessage.Header();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage=new ProtocolMessage<>(header,rpcResponse);
            try{
                Buffer encode= ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            }catch (IOException e){
                throw new RuntimeException("协议消息编码失败");
            }
        });
    }
}
