package com.polyu.blockchain.web.controller;


import com.polyu.blockchain.chain.Block;
import com.polyu.blockchain.chain.MainChain;
import com.polyu.blockchain.chain.transaction.Output;
import com.polyu.blockchain.chain.transaction.Transaction;
import com.polyu.blockchain.chain.transaction.Wallet;
import com.polyu.blockchain.common.util.KeyUtil;
import com.polyu.blockchain.common.vo.NewAccountVo;
import com.polyu.blockchain.common.vo.TransferReqVo;
import com.polyu.blockchain.common.wrapper.Result;
import com.polyu.blockchain.p2p.netty.BusinessHandler;
import com.polyu.blockchain.web.service.RequestService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class RequestController {
    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    private RequestService blockChainService;

    @RequestMapping(value = "/obtainNewAccount")
    public Result<NewAccountVo> getNewAccount() {
        try {
            NewAccountVo newAccountVo = blockChainService.obtainNewAccount();
            return Result.success(newAccountVo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/queryBalance")
    public Result<Float> queryBalance(@RequestParam String publicKey) {
        publicKey = publicKey.replaceAll(" +","+");
        try {
            Float balance = blockChainService.getBalance(publicKey);
            return Result.success(balance);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/transfer")
    public Result<Boolean> transfer(@RequestBody TransferReqVo request) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    blockChainService.transfer(request.getFromPublic(),
                            request.getFromPrivate(),
                            request.getToPublic(),
                            request.getValue());
                }
            }).start();
            return Result.success(true, "Server started to process.");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Deprecated
    @RequestMapping(value = "/init")
    public Result<Boolean> init() {
        Wallet walletA = new Wallet();
        Wallet coinbase = new Wallet();
        MainChain.genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 1000f, null);
        MainChain.genesisTransaction.generateSignature(coinbase.privateKey);     //manually sign the genesis transaction
        MainChain.genesisTransaction.transactionId = "0"; //manually set the transaction id
        MainChain.genesisTransaction.outputs.add(new Output(MainChain.genesisTransaction.recipient, MainChain.genesisTransaction.value, MainChain.genesisTransaction.transactionId)); //manually add the Transactions Output
        MainChain.UTXOs.put(MainChain.genesisTransaction.outputs.get(0).id, MainChain.genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        log.info("Creating and Mining Genesis block.");
        Block genesis = new Block("0");
        genesis.addTransaction(MainChain.genesisTransaction);
        MainChain.addBlock(genesis);
        log.warn("init wallet public key: {}", KeyUtil.PublicKeyToString(walletA.publicKey));
        log.warn("init wallet private key: {}", KeyUtil.PrivateKeyToString(walletA.privateKey));
        BusinessHandler.synChain(MainChain.blockChain, MainChain.UTXOs, MainChain.genesisTransaction);
        return Result.success(true);
    }

}
