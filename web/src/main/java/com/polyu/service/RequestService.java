package com.polyu.service;

import com.polyu.chain.facade.BlockChainService;
import com.polyu.chain.facade.impl.BlockChainServiceImp;
import com.polyu.vo.NewAccountVo;
import org.springframework.stereotype.Service;


@Service
public class RequestService {

    private BlockChainService blockChainService = new BlockChainServiceImp();

    public NewAccountVo obtainNewAccount() {
        return blockChainService.obtainNewAccount();
    }

    public float getBalance(String publicKeyStr) {
        return blockChainService.getBalance(publicKeyStr);
    }

    public void transfer(String from, String fromPKey, String to, float amount) {
        blockChainService.transfer(from, fromPKey, to, amount);
    }
}
