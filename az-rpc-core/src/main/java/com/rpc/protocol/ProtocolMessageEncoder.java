package com.rpc.protocol;

import com.rpc.enums.ProtocolMessageSerializerEnum;
import com.rpc.serializer.Serializer;
import com.rpc.serializer.factory.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageEncoder {

    /**
     * 编码 作用: 将协议消息编码为字节流: ProtocolMessage -> Buffer0;  将协议信息转为二进制流
     * @param protocolMessage
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if(protocolMessage==null || protocolMessage.getHeader()==null){
            return Buffer.buffer();
        }
        ProtocolMessage.Header header=protocolMessage.getHeader();
        //依次向缓存写入字节
        Buffer buffer=Buffer.buffer();

        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        //获取序列化器
        ProtocolMessageSerializerEnum serializerEnum=ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum==null){
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer= SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes=serializer.serialize(protocolMessage.getBody());
        //写入body长度和数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
