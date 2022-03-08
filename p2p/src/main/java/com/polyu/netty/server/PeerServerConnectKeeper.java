package com.polyu.netty.server;

import com.polyu.wrapper.RegistryPackage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PeerServerConnectKeeper {
    private static List<Channel> peerServers = new CopyOnWriteArrayList<>();
    private static Map<RegistryPackage, Channel> registryPackage2ChannelMap = new ConcurrentHashMap<>();

    synchronized static void add(Channel channel, RegistryPackage registryPackage) {
        if (registryPackage2ChannelMap.containsKey(registryPackage)) {
            return;
        }
        registryPackage2ChannelMap.put(registryPackage, channel);
        peerServers.add(channel);
    }

    public static void remove(RegistryPackage registryPackage) {
        Channel channel = registryPackage2ChannelMap.get(registryPackage);
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        registryPackage2ChannelMap.remove(registryPackage);
        peerServers.remove(channel);
    }

    public static List<Channel> getList() {
        return peerServers;
    }

    public static Map<RegistryPackage, Channel> getMap() {
        return registryPackage2ChannelMap;
    }
}
