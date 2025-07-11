package com.rpc.protocol;

import com.rpc.constant.ProtocolConstant;
import com.rpc.enums.ProtocolMessageSerializerEnum;
import com.rpc.enums.ProtocolMessageTypeEnum;
import com.rpc.model.RpcRequest;
import com.rpc.model.RpcResponse;
import com.rpc.serializer.Serializer;
import com.rpc.serializer.factory.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageDecoder {


    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException{

        //分别从指定位置读出Buffer
        ProtocolMessage.Header header=new ProtocolMessage.Header();
        byte magic=buffer.getByte(0);

        //校验魔数
        if(magic!= ProtocolConstant.PROTOCOL_MAGIC ){
            throw new IOException("非法协议");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));//占8字节
        header.setBodyLength(buffer.getInt(13));
        //解决:粘包,只读指定长度的数据----消息头与消息体的粘包问题
        byte[] bodyBytes=buffer.getBytes(17,17+header.getBodyLength());//字节17---后面
        //解析消息体
        ProtocolMessageSerializerEnum serializerEnum=ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum==null){
            throw new IOException("序列化消息的协议不存在");
        }
        Serializer serializer= SerializerFactory.getInstance(serializerEnum.getValue());


        ProtocolMessageTypeEnum messageTypeEnum=ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if(messageTypeEnum==null){
            throw new RuntimeException("序列化消息的类型不存在");
        }


        //根据消息类型,反序列化消息体
        switch (messageTypeEnum){
            case REQUEST:
                RpcRequest request=serializer.deserialize(bodyBytes,RpcRequest.class);
                return new ProtocolMessage<>(header,request);
            case RESPONSE:
                RpcResponse response=serializer.deserialize(bodyBytes,RpcResponse.class);
                return new ProtocolMessage<>(header,response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持的消息类型");
        }
    }
}
