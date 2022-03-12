package com.polyu.blockchain.chain.transaction;

import com.polyu.blockchain.chain.MainChain;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
public class Wallet {
    private static final Logger log = LoggerFactory.getLogger(Wallet.class);

    private static final String SECURE_RANDOM_MODE = "SHA1PRNG";
    private static final String KEY_PAIR_ALG = "ECDSA";
    private static final String KEY_PAIR_ALG_PROVIDER = "BC";
    private static final String EC_GEN_PARAM = "prime192v1";

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String, Output> UTXOs = new HashMap<>();

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public Wallet() {
        generateKeyPair();
    }

    public Wallet(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Wallet(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_PAIR_ALG, KEY_PAIR_ALG_PROVIDER);
            SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_MODE);
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(EC_GEN_PARAM);
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0.0f;
        for (Map.Entry<String, Output> item : MainChain.UTXOs.entrySet()) {
            Output UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                // add it to our list of unspent transactions.
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey _recipient, float value) {
        if (getBalance() < value) {
            log.warn("Not Enough funds to send transaction, transaction Discarded.");
            return null;
        }
        ArrayList<Input> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, Output> item : UTXOs.entrySet()) {
            Output UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new Input(UTXO.id));
            if (total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (Input input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }
}
