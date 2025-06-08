package com.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMessage <T>{
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private T body;


    //一共4字节长度
    @Data
    public static class Header{
        /**
         * 魔数
         */
        private byte magic; //1
        /**
         * 版本号
         */
        private byte version; //1
        /**
         * 序列化器
         */
        private byte serializer; //1
        /**
         * 消息类型
         */
        private byte type;  //1
        /**
         * 状态
         */
        private byte status;  //1
        /**
         * 请求id
         */
        private long requestId;  //8
        /**
         * 消息体长度
         */
        private int bodyLength;  //4

    }
}
