package com.polyu.blockchain.p2p.registry;


import com.polyu.blockchain.common.config.NameSpaceEnum;
import com.polyu.blockchain.common.wrapper.RegistryPackage;
import com.polyu.blockchain.p2p.netty.client.Connector;

import java.util.List;

public class ServerDiscovery {
    private CuratorClient zkClient;
    private List<String> nodeList;

    public ServerDiscovery(String registryAddress) throws Exception {
        this.zkClient = new CuratorClient(registryAddress, 5000);
        pullServerInfo();
    }

    public void connectServer(int index) throws Exception {
        String node = nodeList.get(index);
        byte[] bytes = zkClient.getData(NameSpaceEnum.ZK_REGISTRY_PATH.getValue().concat("/").concat(node));
        String json = new String(bytes);
        RegistryPackage registryPackage = RegistryPackage.fromJson(json);
        Connector.connect(registryPackage.getHost(), registryPackage.getPort());
    }

    public void pullServerInfo() throws Exception {
        this.nodeList = zkClient.getChildren(NameSpaceEnum.ZK_REGISTRY_PATH.getValue());
    }

    public List<String> getNodeList() {
        return nodeList;
    }

}
