package com.polyu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class HashUtil {
    private static final String ENCODE_ALGORITHM = "SHA-256";
    private static final String ENCODE_FORMAT = "UTF-8";
    private static final Logger logger = LoggerFactory.getLogger(HashUtil.class);

    /**
     * SHA-256 加密
     * @param text input
     * @return
     */
    public static String encode(String text) {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            byte[] hash = digest.digest(text.getBytes(ENCODE_FORMAT));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return hexString.toString();
    }
}
