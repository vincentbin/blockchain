package com.polyu.netty;

import com.polyu.config.NameSpaceEnum;
import com.polyu.netty.client.Connector;
import com.polyu.netty.server.PeerServerConnectKeeper;
import com.polyu.registry.CuratorClient;
import com.polyu.wrapper.RegistryPackage;
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
import java.util.List;

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
                        Connector.connect(rpAdd.getHost(), rpAdd.getPort());
                        break;
                    case CHILD_REMOVED:
                        RegistryPackage rpRemove = RegistryPackage.fromJson(new String(childData.getData(), StandardCharsets.UTF_8));
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

//    public void pullServerInfo() throws Exception {
//        if (!zkClient.checkPath(NameSpaceEnum.ZK_REGISTRY_PATH.getValue())) {
//            return;
//        }
//        List<String> nodeList = zkClient.getChildren(NameSpaceEnum.ZK_REGISTRY_PATH.getValue());
//        for (String node : nodeList) {
//            byte[] bytes = zkClient.getData(NameSpaceEnum.ZK_REGISTRY_PATH.getValue().concat("/").concat(node));
//            String json = new String(bytes);
//            RegistryPackage registryPackage = RegistryPackage.fromJson(json);
//            NettyPeerServerConnector.connect(registryPackage.getHost(), registryPackage.getPort());
//        }
//    }

}
