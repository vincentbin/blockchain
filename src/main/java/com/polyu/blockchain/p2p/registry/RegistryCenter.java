package com.polyu.blockchain.p2p.registry;

import com.polyu.blockchain.common.config.NameSpaceEnum;
import com.polyu.blockchain.common.wrapper.RegistryPackage;
import com.polyu.blockchain.p2p.netty.PeerServerConnectKeeper;
import com.polyu.blockchain.p2p.netty.client.Connector;
import io.netty.channel.Channel;
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
import java.util.Map;

public class RegistryCenter {
    private static final Logger logger = LoggerFactory.getLogger(RegistryCenter.class);

    private CuratorClient zkClient;
    private static String host;
    private static int port;
    private String zkPath;

    public RegistryCenter(String registryAddress) {
        this.zkClient = new CuratorClient(registryAddress, 5000);
    }

    /**
     * register server & observer active online server
     * @param host local ip
     * @param port local port
     * @return 注册前是否有活跃线上机器
     * @throws Exception zk watch
     */
    public boolean registerService(String host, int port) throws Exception {
        RegistryCenter.host = host;
        RegistryCenter.port = port;
        List<String> activeList = getServiceList();
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
                        Map<RegistryPackage, Channel> map = PeerServerConnectKeeper.getMap();
                        if (map.containsKey(rpAdd)) {
                            break;
                        }
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
        return activeList.isEmpty();
    }

    public void unregisterService() {
        try {
            this.zkClient.deletePath(zkPath);
        } catch (Exception e) {
            logger.error("Delete failed error: {}.", e.getMessage(), e);
        }
        this.zkClient.close();
    }

    /**
     * get active server list
     * @return server list
     */
    private List<String> getServiceList() {
        List<String> nodeList = null;
        try {
            nodeList = zkClient.getChildren(NameSpaceEnum.ZK_REGISTRY_PATH.getValue());
            logger.info("online server list: {}.", nodeList.toString());
        } catch (Exception e) {
            logger.error("Get node exception: {}.", e.getMessage());
        }
        return nodeList;
    }

}
