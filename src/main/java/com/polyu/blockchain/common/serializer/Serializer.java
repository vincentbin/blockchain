package com.polyu.blockchain.common.serializer;

import java.io.IOException;

public interface Serializer {

    /**
     * 序列化
     * @param obj 对象
     * @return 字节数组
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param bytes 字节数组
     * @param clazz .class 类型
     * @return 原对象
     */
    <T> Object deserialize(byte[] bytes, Class<T> clazz) throws IOException;

}
