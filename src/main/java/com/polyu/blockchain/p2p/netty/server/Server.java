package com.polyu.blockchain.p2p.netty.server;


import com.polyu.blockchain.common.p2p.P2PHolder;
import com.polyu.blockchain.p2p.netty.BusinessHandler;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class Server implements ApplicationContextAware {

    @Value("${server.netty.local.address}")
    private String serverAddress;

    @Value("${server.netty.registry.address}")
    private String registryAddress;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        String serverAddress = "127.0.0.1:8001";
//        String registryAddress = "127.0.0.1:2181";
        Thread thread = new Thread(new BootStrap(serverAddress, registryAddress));
        P2PHolder.setHandler(BusinessHandler.getInstance());
        thread.start();
    }
}
