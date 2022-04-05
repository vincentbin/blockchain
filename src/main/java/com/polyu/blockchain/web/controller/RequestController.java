package com.polyu.blockchain.web.controller;


import com.polyu.blockchain.common.vo.NewAccountVo;
import com.polyu.blockchain.common.vo.TransferReqVo;
import com.polyu.blockchain.common.wrapper.Result;
import com.polyu.blockchain.web.service.RequestService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class RequestController {
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

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

}
