package com.polyu.netty.server;

import com.polyu.netty.BusinessHandler;
import com.polyu.p2p.P2PHolder;

public class Server {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1:8000";
        String registryAddress = "127.0.0.1:2181";
        Thread thread = new Thread(new BootStrap(serverAddress, registryAddress));
        P2PHolder.setHandler(BusinessHandler.getInstance());
        thread.start();
    }
}
