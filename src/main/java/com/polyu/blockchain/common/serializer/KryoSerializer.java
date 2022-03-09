package com.polyu.blockchain.common.serializer;



import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.polyu.blockchain.common.wrapper.Message;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {
    private static final KryoPool pool = new KryoPool.Builder(new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(Message.class);
            Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    }).build();

    private static final KryoSerializer serializer = new KryoSerializer();

    public static KryoSerializer getInstance() {
        return KryoSerializer.serializer;
    }

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        Kryo kryo = pool.borrow();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output out = new Output(byteArrayOutputStream);
        try {
            kryo.writeObject(out, obj);
            out.close();
        } finally {
            byteArrayOutputStream.close();
            pool.release(kryo);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        Kryo kryo = pool.borrow();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input in = new Input(byteArrayInputStream);
        Object result;
        try {
            result = kryo.readObject(in, clazz);
            in.close();
        } finally {
            byteArrayInputStream.close();
            pool.release(kryo);
        }
        return result;
    }
}
