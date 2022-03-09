package com.polyu.blockchain.common.codec;

import com.polyu.blockchain.common.serializer.Serializer;
import com.polyu.blockchain.common.wrapper.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

public class Encoder extends MessageToByteEncoder {
    private Serializer serializer;

    public Encoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws IOException {
        if (!(msg instanceof Message)) {
            return;
        }
        byte[] data = serializer.serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

}
