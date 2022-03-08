package com.polyu.test;

import com.polyu.netty.server.BootStrap;

public class Test {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1:17777";
        String registryAddress = "127.0.0.1:2181";
        new Thread(new BootStrap(serverAddress, registryAddress)).start();
    }
}
