package com.polyu.controller;

import com.polyu.chain.facade.BlockChainService;
import com.polyu.service.RequestService;
import com.polyu.vo.NewAccountVo;
import com.polyu.vo.TransferReqVo;
import com.polyu.wrapper.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RequestController {

    @Autowired
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
            return Result.success(true);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

}
