package com.polyu.chain.facade;

import com.polyu.vo.NewAccountVo;

/**
 * Basic Service Interface
 */
public interface BlockChainService {

    NewAccountVo obtainNewAccount();

    float getBalance(String publicKey);

    void transfer(String from, String fromPKey, String to, float amount);

}
