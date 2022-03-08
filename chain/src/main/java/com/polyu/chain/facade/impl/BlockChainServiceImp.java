package com.polyu.chain.facade.impl;

import com.polyu.chain.MainChain;
import com.polyu.chain.Wallet;
import com.polyu.chain.Block;
import com.polyu.chain.facade.BlockChainService;
import com.polyu.p2p.Handler;
import com.polyu.p2p.P2PHolder;
import com.polyu.util.KeyUtil;
import com.polyu.vo.NewAccountVo;

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
        Handler handler = P2PHolder.getHandler();
        handler.processBroadcast(block);
    }

}
