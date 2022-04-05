package com.polyu.blockchain.p2p.netty.server;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Server implements InitializingBean {

    @Value("${server.netty.local.address}")
    private String serverAddress;

    @Value("${server.netty.registry.address}")
    private String registryAddress;

    @Override
    public void afterPropertiesSet() {
        Thread thread = new Thread(new BootStrap(serverAddress, registryAddress));
        thread.start();
    }
}
