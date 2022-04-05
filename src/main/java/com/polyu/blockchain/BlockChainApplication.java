package com.polyu.blockchain;

import com.polyu.blockchain.common.util.KeyUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlockChainApplication {

    public static void main(String[] args) {
        // syn chain check
        /**
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    Thread.sleep(3000);
                    System.out.println("MainChain.blockChain = " + MainChain.blockChain);
                }
            }
        }).start();
         */
        SpringApplication.run(BlockChainApplication.class, args);
    }
}
