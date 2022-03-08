package com.polyu.netty;

import com.polyu.wrapper.RegistryPackage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerServerConnectKeeper {
    private static Map<RegistryPackage, Channel> registryPackage2ChannelMap = new ConcurrentHashMap<>();

    public synchronized static void add(Channel channel, RegistryPackage registryPackage) {
        if (registryPackage2ChannelMap.containsKey(registryPackage)) {
            return;
        }
        registryPackage2ChannelMap.put(registryPackage, channel);
    }

    public static void remove(RegistryPackage registryPackage) {
        Channel channel = registryPackage2ChannelMap.get(registryPackage);
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        registryPackage2ChannelMap.remove(registryPackage);
    }

    public static Map<RegistryPackage, Channel> getMap() {
        return registryPackage2ChannelMap;
    }
}
