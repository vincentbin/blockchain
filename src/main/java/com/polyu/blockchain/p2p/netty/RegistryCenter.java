package com.polyu.blockchain.p2p.netty;

import com.polyu.blockchain.common.config.NameSpaceEnum;
import com.polyu.blockchain.common.wrapper.RegistryPackage;
import com.polyu.blockchain.p2p.netty.client.Connector;
import com.polyu.blockchain.p2p.registry.CuratorClient;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RegistryCenter {
    private static final Logger logger = LoggerFactory.getLogger(RegistryCenter.class);

    private CuratorClient zkClient;
    private static String host;
    private static int port;
    private String zkPath;

    public RegistryCenter(String registryAddress) {
        this.zkClient = new CuratorClient(registryAddress, 5000);
    }

    public void registerService(String host, int port) throws Exception {
        RegistryCenter.host = host;
        RegistryCenter.port = port;
        try {
            RegistryPackage registryPackage = new RegistryPackage();
            registryPackage.setHost(RegistryCenter.host);
            registryPackage.setPort(RegistryCenter.port);
            String registryPackageJson = registryPackage.toJson();
            byte[] bytes = registryPackageJson.getBytes();
            String path = NameSpaceEnum.ZK_REGISTRY_PATH.getValue().concat("/node-") + registryPackage.hashCode();
            path = this.zkClient.createPathData(path, bytes);
            this.zkPath = path;
        } catch (Exception e) {
            logger.error("Register fail, exception: {}.", e.getMessage(), e);
        }

        this.zkClient.watchPathChildrenNode(NameSpaceEnum.ZK_REGISTRY_PATH.getValue(), new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) {
                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                ChildData childData = pathChildrenCacheEvent.getData();
                switch (type) {
                    case CHILD_ADDED:
                        RegistryPackage rpAdd = RegistryPackage.fromJson(new String(childData.getData(), StandardCharsets.UTF_8));
                        // connect new node
                        Connector.connect(rpAdd.getHost(), rpAdd.getPort());
                        break;
                    case CHILD_REMOVED:
                        RegistryPackage rpRemove = RegistryPackage.fromJson(new String(childData.getData(), StandardCharsets.UTF_8));
                        // p2p delete removed node
                        PeerServerConnectKeeper.remove(rpRemove);
                        break;
                }
            }
        });

        this.zkClient.addConnectionStateListener(new ConnectionStateListener() {
            @SneakyThrows
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.RECONNECTED) {
                    logger.info("Connection state: {}, reconnected.", newState);
                    registerService(RegistryCenter.host, RegistryCenter.port);
                }
            }
        });
    }

    public void unregisterService() {
        try {
            this.zkClient.deletePath(zkPath);
        } catch (Exception e) {
            logger.error("Delete failed error: {}.", e.getMessage(), e);
        }
        this.zkClient.close();
    }

}
