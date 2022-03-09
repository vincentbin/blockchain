package com.polyu.blockchain.chain.facade.impl;



import com.polyu.blockchain.chain.Block;
import com.polyu.blockchain.chain.MainChain;
import com.polyu.blockchain.chain.transaction.Wallet;
import com.polyu.blockchain.chain.facade.BlockChainService;
import com.polyu.blockchain.common.util.KeyUtil;
import com.polyu.blockchain.common.vo.NewAccountVo;
import com.polyu.blockchain.p2p.netty.BusinessHandler;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class BlockChainServiceImp implements BlockChainService {
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
        block.addTransaction(issuerWallet.sendFunds(KeyUtil.stringToPublicKey(to), amount));
        BusinessHandler.mineBroadcast(block);
    }

}
