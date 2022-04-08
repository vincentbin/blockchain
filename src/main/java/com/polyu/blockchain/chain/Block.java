package com.polyu.blockchain.chain;


import com.polyu.blockchain.chain.transaction.Transaction;
import com.polyu.blockchain.common.util.StringUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Block {
    private static final Logger log = LoggerFactory.getLogger(Block.class);

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public long timeStamp;
    public int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
    }

    /**
     * calculate new hash based on blocks contents
     * @return hash
     */
    public String calculateHash() {
        return StringUtil.applySha256(previousHash + timeStamp + nonce + merkleRoot);
    }

    /**
     * increases nonce value until hash target is reached.
     * @param difficulty mine difficulty
     */
    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty); //Create a string with difficulty * "0"
        while (hash == null || !hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        log.info("Block Mined!!! : {}.", hash);
    }

    /**
     * add transactions to this block
     * @param transaction new transaction
     * @return success?
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        if (!"0".equals(previousHash)) {
            if (!transaction.processTransaction()) {
                log.info("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        log.info("Transaction Successfully added to Block");
        return true;
    }
}
