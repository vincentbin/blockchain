package com.polyu.netty.client;

import com.polyu.registry.ServerDiscovery;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BootStrap implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BootStrap.class);

    private static String registryAddress;
    private static String clientName;
    private static volatile ServerDiscovery serverDiscovery;

    BootStrap(String registryAddress, String clientName) {
        BootStrap.registryAddress = registryAddress;
        BootStrap.clientName = clientName;
    }

    @SneakyThrows
    @Override
    public void run() {
        if (BootStrap.serverDiscovery == null) {
            BootStrap.serverDiscovery = new ServerDiscovery(BootStrap.registryAddress);
        } else {
            BootStrap.serverDiscovery.pullServerInfo();
        }
        int size = serverDiscovery.getNodeList().size();
        int serverIndex = Math.abs(Objects.hash(BootStrap.clientName) % size);
        serverDiscovery.connectServer(serverIndex);
    }

    public static String getClientName() {
        return clientName;
    }
}
