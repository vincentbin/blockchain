package com.polyu.netty;

import com.polyu.codec.Decoder;
import com.polyu.codec.Encoder;
import com.polyu.serializer.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel ch) {
        ChannelPipeline cp = ch.pipeline();
        cp.addLast(new Decoder(KryoSerializer.getInstance()));
        cp.addLast(new Encoder(KryoSerializer.getInstance()));
        cp.addLast(new BusinessHandler());
    }

}
