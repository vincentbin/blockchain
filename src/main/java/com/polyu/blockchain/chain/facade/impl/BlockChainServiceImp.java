package com.polyu.blockchain.chain.facade.impl;



import com.polyu.blockchain.chain.Block;
import com.polyu.blockchain.chain.MainChain;
import com.polyu.blockchain.chain.transaction.Output;
import com.polyu.blockchain.chain.transaction.Transaction;
import com.polyu.blockchain.chain.transaction.Wallet;
import com.polyu.blockchain.chain.facade.BlockChainService;
import com.polyu.blockchain.common.util.KeyUtil;
import com.polyu.blockchain.common.vo.NewAccountVo;
import com.polyu.blockchain.p2p.netty.handler.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class BlockChainServiceImp implements BlockChainService {
    private static final Logger log = LoggerFactory.getLogger(BlockChainServiceImp.class);

    @Override
    public NewAccountVo obtainNewAccount() {
        Wallet wallet = new Wallet();
        PublicKey publicKey = wallet.getPublicKey();
        PrivateKey privateKey = wallet.getPrivateKey();
        return new NewAccountVo(KeyUtil.PublicKeyToString(publicKey),
                KeyUtil.PrivateKeyToString(privateKey));
    }

    @Override
    public float getBalance(String publicKeyStr) {
        PublicKey publicKey = KeyUtil.stringToPublicKey(publicKeyStr);
        Wallet wallet = new Wallet(publicKey);
        return wallet.getBalance();
    }

    @Override
    public void transfer(String from, String fromPKey, String to, float amount) {
        ArrayList<Block> blockChain = MainChain.getBlockChain();
        Block block;
        if (blockChain.isEmpty()) {
            block = new Block("0");
        } else {
            block = new Block(blockChain.get(blockChain.size() - 1).hash);
        }
        Wallet issuerWallet = new Wallet(KeyUtil.stringToPrivateKey(fromPKey), KeyUtil.stringToPublicKey(from));
        Transaction transaction = issuerWallet.sendFunds(KeyUtil.stringToPublicKey(to), amount);
        if (transaction == null) {
            log.warn("transaction failed, it will not be published to p2p network.");
            return;
        }
        block.addTransaction(issuerWallet.sendFunds(KeyUtil.stringToPublicKey(to), amount));
        ServerHandler.mineBroadcast(block);
    }

    /**
     * init genesis wallet
     */
    public static void init() {
        Wallet walletA = new Wallet();
        Wallet baseWallet = new Wallet();
        MainChain.genesisTransaction = new Transaction(baseWallet.publicKey, walletA.publicKey, 1000f, null);
        MainChain.genesisTransaction.generateSignature(baseWallet.privateKey);     // manually sign the genesis transaction
        MainChain.genesisTransaction.transactionId = "0"; // manually set the transaction id
        MainChain.genesisTransaction.outputs.add(new Output(MainChain.genesisTransaction.recipient, MainChain.genesisTransaction.value, MainChain.genesisTransaction.transactionId)); //manually add the Transactions Output
        MainChain.UTXOs.put(MainChain.genesisTransaction.outputs.get(0).id, MainChain.genesisTransaction.outputs.get(0)); // its important to store our first transaction in the UTXOs list.

        log.info("Creating and Mining Genesis block.");
        Block genesis = new Block("0");
        genesis.addTransaction(MainChain.genesisTransaction);
        MainChain.addBlock(genesis);
        log.warn("init wallet public key: {}", KeyUtil.PublicKeyToString(walletA.publicKey));
        log.warn("init wallet private key: {}", KeyUtil.PrivateKeyToString(walletA.privateKey));
    }

}
