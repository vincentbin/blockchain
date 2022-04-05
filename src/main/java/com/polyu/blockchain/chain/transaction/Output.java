package com.polyu.blockchain.chain.transaction;

import com.polyu.blockchain.common.util.StringUtil;
import lombok.Data;
import lombok.ToString;

import java.security.PublicKey;

@Data
@ToString
public class Output {

    public String id;

    /**
     * new owner of these coins.
     */
    public transient PublicKey recipient;

    public String recipientStr;

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
