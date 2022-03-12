package com.polyu.blockchain.chain.transaction;

import lombok.Data;

@Data
public class Input {

    /**
     * output transactionId
     */
    public String transactionOutputId;

    /**
     * unspent transaction output
     */
    public Output UTXO;

    public Input(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
