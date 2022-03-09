package com.polyu.blockchain.chain.transaction;

import com.polyu.blockchain.chain.MainChain;
import com.polyu.blockchain.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;


public class Transaction {
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);

    public String transactionId; //Contains a hash of transaction*
    public PublicKey sender; //Senders address/public key.
    public PublicKey recipient; //Recipients address/public key.
    public float value; //Contains the amount we wish to send to the recipient.
    private byte[] signature; //This is to prevent anybody else from spending funds in our wallet.

    public ArrayList<Input> inputs;
    public ArrayList<Output> outputs = new ArrayList<>();

    private static final float minimumTransaction = 0.1f;

    private static int sequence = 0; //A rough count of how many transactions have been generated

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<Input> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction() {

        if (!verifySignature()) {
            log.warn("#Transaction Signature failed to verify");
            return false;
        }

        // Gathers transaction inputs (Making sure they are unspent):
        for (Input i : inputs) {
            i.UTXO = MainChain.getUTXOs().get(i.transactionOutputId);
        }

        // Checks if transaction is valid:
        if (getInputsValue() < minimumTransaction) {
            log.info("Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //Generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new Output(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new Output(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        // Add outputs to Unspent list
        for (Output o : outputs) {
            MainChain.getUTXOs().put(o.id, o);
        }

        // Remove transaction inputs from UTXO lists as spent:
        for (Input i : inputs) {
            if (i.UTXO == null) continue; // if Transaction can't be found skip it
            MainChain.getUTXOs().remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (Input i : inputs) {
            if (i.UTXO == null) {
                continue; // if Transaction can't be found skip it, This behavior may not be optimal.
            }
            total += i.UTXO.value;
        }
        return total;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public float getOutputsValue() {
        float total = 0;
        for (Output o : outputs) {
            total += o.value;
        }
        return total;
    }

    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) + value + sequence);
    }
}
