package com.polyu.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Chain {
    private static final Logger logger = LoggerFactory.getLogger(Chain.class);

    private static List<Block> blocks = new LinkedList<>();

    public static void main(String[] args) {
        blocks.add(new Block("Hi im the first block", "0"));
        System.out.println("Trying to Mine block 1... ");
        //blocks.get(0).mineBlock(difficulty);

        blocks.add(new Block("Yo im the second block",blocks.get(blocks.size()-1).getHash()));
        System.out.println("Trying to Mine block 2... ");
        //blocks.get(1).mineBlock(difficulty);

        blocks.add(new Block("Hey im the third block", blocks.get(blocks.size()-1).getHash()));
        System.out.println("Trying to Mine block 3... ");
        //blocks.get(2).mineBlock(difficulty);

        System.out.println("Blockchain is Valid: " + isValid());

        System.out.println("blocks = " + blocks);

    }

    /**
     * 验证区块链有效性
     * @return
     */
    public static boolean isValid() {
        if (blocks.isEmpty()) {
            return true;
        }
        Iterator<Block> iterator = blocks.iterator();
        Block pre = iterator.next();
        if (!pre.getHash().equals(pre.calHash())) {
            logger.warn("current block is changed.");
            return false;
        }
        while (iterator.hasNext()) {
            Block cur = iterator.next();
            if (!cur.getHash().equals(cur.calHash())) {
                logger.warn("current block is changed.");
                return false;
            }

            if (!pre.getHash().equals(cur.getPreviousHash())) {
                logger.warn("cur's pre Hash value is false.");
                return false;
            }
            pre = cur;
        }
        return true;
    }

}
