package com.polyu.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Message {
    private boolean clientInitMsg;

    private boolean addressQuery;
    private boolean addressReply;
    private String ip;
    private int port;

    private boolean mineRequest;
    private boolean mineReply;
    private String uuid;
    private Object content;

    public Message(boolean addressQuery) {
        this.addressQuery = addressQuery;
    }

    public Message(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
