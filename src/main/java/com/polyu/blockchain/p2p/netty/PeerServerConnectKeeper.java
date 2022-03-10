package com.polyu.blockchain.p2p.netty;

import com.polyu.blockchain.common.wrapper.RegistryPackage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PeerServerConnectKeeper {
    private static Map<RegistryPackage, Channel> registryPackage2ChannelMap = new ConcurrentHashMap<>();

    private static Set<String> uuidSet = new HashSet<>();
    private static boolean needSynChain;

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

    public static Set<String> getUuidSet() {
        return uuidSet;
    }

    public static boolean isNeedSynChain() {
        return needSynChain;
    }

    public static void setNeedSynChain(boolean needSynChain) {
        PeerServerConnectKeeper.needSynChain = needSynChain;
    }
}
