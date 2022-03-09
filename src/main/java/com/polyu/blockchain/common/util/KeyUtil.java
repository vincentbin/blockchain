package com.polyu.blockchain.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {
    private static final Logger log = LoggerFactory.getLogger(KeyUtil.class);

    public static String PublicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String PrivateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey stringToPublicKey(String publicKeyStr) {
        try {
            byte[] decode = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static PrivateKey stringToPrivateKey(String privateKeyStr) {
        try {
            byte[] decode = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
