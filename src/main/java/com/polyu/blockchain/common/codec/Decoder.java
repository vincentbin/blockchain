package com.polyu.blockchain.common.codec;

import com.polyu.blockchain.common.serializer.Serializer;
import com.polyu.blockchain.common.wrapper.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class Decoder extends ByteToMessageDecoder {
    private Serializer serializer;

    public Decoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj;
        obj = serializer.deserialize(data, Message.class);
        out.add(obj);
    }

}
