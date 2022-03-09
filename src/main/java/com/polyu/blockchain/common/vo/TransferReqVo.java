package com.polyu.blockchain.common.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransferReqVo {
    private String fromPublic;
    private String fromPrivate;
    private String toPublic;
    private Float value;
}
