package com.polyu.blockchain.common.config;

public enum NameSpaceEnum {

    ZK_REGISTRY_PATH("/registry"),
    ZK_NAME_SPACE("BLOCK_CHAIN_P2P"),
    ZK_SESSION_TIMEOUT(5000),
    ZK_CONNECTION_TIMEOUT(5000);


    private String value;
    private int timeOutLength;

    NameSpaceEnum(String value) {
        this.value = value;
    }

    NameSpaceEnum(int timeOutLength) {
        this.timeOutLength = timeOutLength;
    }

    public String getValue() {
        return value;
    }

    public int getTimeOutLength() {
        return timeOutLength;
    }

}