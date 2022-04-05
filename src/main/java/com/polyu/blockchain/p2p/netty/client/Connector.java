package com.polyu.blockchain.p2p.netty.client;

import com.polyu.blockchain.common.wrapper.RegistryPackage;
import com.polyu.blockchain.p2p.netty.ClientChannelInitializer;
import com.polyu.blockchain.p2p.netty.PeerServerConnectKeeper;
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

/**
 * 启动后 zk 增量获取新上线机器 在 Connector 进行连接
 */
public class Connector {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);
    private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

    public static void connect(String serverIp, int serverPort) {
        InetSocketAddress remotePeer = new InetSocketAddress(serverIp, serverPort);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer());

        ChannelFuture channelFuture = bootstrap.connect(remotePeer);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    logger.info("client connect server succeed.");
                    Channel channel = channelFuture.channel();
                    PeerServerConnectKeeper.add(channel, new RegistryPackage(serverIp, serverPort));
                } else {
                    logger.error("client connect server failed.");
                }
            }
        });
    }
}
