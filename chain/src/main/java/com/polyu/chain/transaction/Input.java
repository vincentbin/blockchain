package com.polyu.chain.transaction;

public class Input {
    public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    public Output UTXO; //Contains the Unspent transaction output

    public Input(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
