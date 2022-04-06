package com.polyu.blockchain.web.service;


import com.polyu.blockchain.chain.facade.BlockChainService;
import com.polyu.blockchain.common.vo.NewAccountVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class RequestService {

    private BlockChainService blockChainService;

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
