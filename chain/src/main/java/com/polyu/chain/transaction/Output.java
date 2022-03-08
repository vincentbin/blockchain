package com.polyu.chain.transaction;

import com.polyu.chain.utils.StringUtil;

import java.security.PublicKey;

public class Output {

    public String id;

    /**
     * new owner of these coins.
     */
    public PublicKey recipient;

    /**
     * the amount of coins they own
     */
    public float value;

    /**
     * id of the transaction this output was created in
     */
    public String parentTransactionId;

    public Output(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) +
                value + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey.equals(recipient);
    }
}
