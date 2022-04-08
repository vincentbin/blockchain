package com.polyu.blockchain.web.controller;


import com.polyu.blockchain.common.vo.NewAccountVo;
import com.polyu.blockchain.common.vo.TransferReqVo;
import com.polyu.blockchain.common.wrapper.HTTPResult;
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
    public HTTPResult<NewAccountVo> getNewAccount() {
        try {
            NewAccountVo newAccountVo = blockChainService.obtainNewAccount();
            return HTTPResult.success(newAccountVo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return HTTPResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/queryBalance")
    public HTTPResult<Float> queryBalance(@RequestParam String publicKey) {
        publicKey = publicKey.replaceAll(" +","+");
        try {
            Float balance = blockChainService.getBalance(publicKey);
            return HTTPResult.success(balance);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return HTTPResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/transfer")
    public HTTPResult<Boolean> transfer(@RequestBody TransferReqVo request) {
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
            return HTTPResult.success(true, "Server started to process.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return HTTPResult.error(e.getMessage());
        }
    }

}
