package com.polyu.blockchain.p2p.netty;

import com.polyu.blockchain.common.codec.Decoder;
import com.polyu.blockchain.common.codec.Encoder;
import com.polyu.blockchain.common.serializer.KryoSerializer;
import com.polyu.blockchain.p2p.netty.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline cp = ch.pipeline();
        cp.addLast(new Decoder(KryoSerializer.getInstance()));
        cp.addLast(new Encoder(KryoSerializer.getInstance()));
        cp.addLast(new ServerHandler());
    }
}
