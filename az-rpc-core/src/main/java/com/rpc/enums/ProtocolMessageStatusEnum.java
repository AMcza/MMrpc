package com.rpc.enums;

import lombok.Getter;

/**
 * 协议消息的状态枚举
 */
@Getter
public enum ProtocolMessageStatusEnum {

    OK("ok",20),
    BAD_REQUEST("badRequest",40),
    BAD_RESPONSE("badResponse",50);

    private final String  text;


    private final int value;

    ProtocolMessageStatusEnum(String text,int value){
        this.text=text;
        this.value=value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value){
        for(ProtocolMessageStatusEnum statusEnum:ProtocolMessageStatusEnum.values()){
            if(statusEnum.getValue()==value){
                return statusEnum;
            }
        }
        return null;
    }
}
