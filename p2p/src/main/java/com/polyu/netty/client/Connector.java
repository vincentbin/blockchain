package com.polyu.netty.client;

import com.polyu.netty.NettyChannelInitializer;
import com.polyu.wrapper.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Connector {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);
    private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

    public static void connect(String serverIp, int serverPort) {
        InetSocketAddress remotePeer = new InetSocketAddress(serverIp, serverPort);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyChannelInitializer());

        ChannelFuture channelFuture = bootstrap.connect(remotePeer);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    logger.info("client connect server succeed.");
                    Channel channel = channelFuture.channel();
                    Message message = new Message();
                    message.setClientInitMsg(true);
                    channel.writeAndFlush(message);
                } else {
                    logger.error("client connect server failed.");
                }
            }
        });
    }
}
