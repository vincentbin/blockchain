package com.polyu.block;

import com.polyu.util.HashUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Data
@NoArgsConstructor
public class Block {
    private static final Logger logger = LoggerFactory.getLogger(Block.class);

    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private long nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calHash();
    }

    public String calHash() {
        return HashUtil.encode(this.previousHash + this.timeStamp + this.nonce + this.data);
    }

    public static void main(String[] args) {
        new Block("aaa", "0").mineBlock(7);
    }

    /**
     * mine
     * @param difficulty 不要超过 18 Math.pow 所限
     */
    public void mineBlock(int difficulty) {
        long num = (long) Math.pow(10, difficulty);
        String targetPrefix = Long.toString(num).substring(1);
        while (!this.hash.substring(0, difficulty).equals(targetPrefix)) {
            nonce++;
            hash = calHash();
        }
        logger.info("mining success! hash: {}.", this.hash);
    }
}
