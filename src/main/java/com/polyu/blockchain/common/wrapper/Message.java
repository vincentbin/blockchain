package com.polyu.blockchain.common.wrapper;

import com.polyu.blockchain.chain.Block;
import com.polyu.blockchain.chain.transaction.Output;
import com.polyu.blockchain.chain.transaction.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;

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
    private Block content;

    private boolean synChain;
    private ArrayList<Block> blockChain;
    private HashMap<String, Output> UTXOs;
    private Transaction genesisTransaction;

    public Message(boolean addressQuery) {
        this.addressQuery = addressQuery;
    }

    public Message(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
