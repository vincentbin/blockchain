package com.polyu.blockchain.p2p.netty.server;

import com.polyu.blockchain.chain.facade.impl.BlockChainServiceImp;
import com.polyu.blockchain.p2p.netty.ServerChannelInitializer;
import com.polyu.blockchain.p2p.netty.PeerServerConnectKeeper;
import com.polyu.blockchain.p2p.registry.RegistryCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootStrap implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BootStrap.class);

    private static String serverAddress;
    private static RegistryCenter registry;

    public BootStrap(String serverAddress, String registryAddress) {
        BootStrap.serverAddress = serverAddress;
        BootStrap.registry = new RegistryCenter(registryAddress);
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            ChannelFuture future = bootstrap.bind(port).sync();
            boolean empty = registry.registerService(host, port);
            if (empty) {
                BlockChainServiceImp.init();
            } else {
                PeerServerConnectKeeper.setNeedSynChain(true);
            }
            logger.info("Server started on ip: {}, port: {}.", host, port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("Server error, exception: {}.", e.getMessage(), e);
        } finally {
            registry.unregisterService();
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static String getServerAddress() {
        return serverAddress;
    }
}
