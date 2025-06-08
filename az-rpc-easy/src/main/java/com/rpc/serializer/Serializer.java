package com.rpc.serializer;

import java.io.IOException;

public interface Serializer {

    /**
     * 序列化
     * @param t
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T t) throws IOException;

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz) throws IOException;
}
