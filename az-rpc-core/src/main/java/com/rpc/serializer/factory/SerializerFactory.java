package com.rpc.serializer.factory;

import com.rpc.serializer.JDKSerializer;
import com.rpc.serializer.Serializer;
import com.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 */

/**
 * 动态获取序列化器
 * final Serializer serializer = SerializerFactory.getInstance(RpcApplicationConfig.getInstance().getSerializer());
 */
public class SerializerFactory {
    /**
     * 通过SPI机制(用于创建对应的实例)
     */
    static {
        SpiLoader.load(Serializer.class);
    }
    /**
     * 序列化映射(用于实现单例)
     */
//    private static final Map<String,Serializer> KEY_SERIALIZER_MAP = new HashMap<>(){{
//        put(SerializerKeys.JDK,new JDKSerializer());
//        put(SerializerKeys.KRYO,new KryoSerializer());
//        put(SerializerKeys.HESSIAN,new HessianSerializer());
//        put(SerializerKeys.JSON,new JsonSerializer());
//    }};

    /**
     * 默认序列化器
     */
//    private static final Serializer DEFAULT_SERIALIZER1 = KEY_SERIALIZER_MAP.get("jdk");
    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();
    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }

}
