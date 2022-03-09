package com.polyu.blockchain.p2p.netty.server;


import com.polyu.blockchain.common.p2p.P2PHolder;
import com.polyu.blockchain.p2p.netty.BusinessHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class Server implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String serverAddress = "127.0.0.1:8001";
        String registryAddress = "127.0.0.1:2181";
        Thread thread = new Thread(new BootStrap(serverAddress, registryAddress));
        P2PHolder.setHandler(BusinessHandler.getInstance());
        thread.start();
    }
}
