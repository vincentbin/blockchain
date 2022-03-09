package com.polyu.blockchain.p2p.netty.client;

import com.polyu.blockchain.p2p.netty.server.BootStrap;

public class Client {
    private static String registryAddress;
    private static String clientName;

    private static volatile BootStrap bootStrap;

    public static void start(String registryAddress, String clientName) {
        Client.registryAddress = registryAddress;
        Client.clientName = clientName;
        Client.bootStrap = new BootStrap(registryAddress, clientName);
        new Thread(Client.bootStrap).start();
    }

    public static void start() {
        new Thread(Client.bootStrap).start();
    }

}