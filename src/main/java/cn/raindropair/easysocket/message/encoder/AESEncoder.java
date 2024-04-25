package cn.raindropair.easysocket.message.encoder;

import cn.raindropair.easysocket.exception.EasySocketException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @description AES 加密方案
 * @Date 2024/4/9
 * @Author raindrop
 */
@Slf4j
public class AESEncoder {
    public static final String key = "gGJVeCZRiyxDVeBr";

    public static String encode(String msg) {
        return encrypt(msg, key);
    }

    public static String decode(String encodeStr) {
        return decrypt(encodeStr, key);
    }


    /**
     * 加密方法
     *
     * @param msg 需要加密的字符串
     * @param key 密钥
     */
    private static String encrypt(String msg, String key) {
        try {
//            keyCheck(key);
            byte[] raw = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            //"算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            //此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            log.error("AESEncoder encrypt error. e=", e);
        }

        return "";
    }

    /**
     * 解密
     *
     * @param encodeMsg 解密字符串
     * @param key       密钥
     */
    private static String decrypt(String encodeMsg, String key) {
        try {
            byte[] raw = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            //先用base64解密
            byte[] encrypted1 = Base64.getDecoder().decode(encodeMsg);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AESEncoder decrypt error. e=", e);
        }
        return "";
    }

    /**
     * 对Key进行校验
     *
     * @param key 密钥
     */
    private void keyCheck(String key) {
        // 判断Key是否正确
        if (key == null) {
            log.error("AESEncoder keyCheck key error.");
            throw new EasySocketException("key error");
        }
        // 判断Key是否为16位或者16的倍数
        if (key.length() % 16 != 0) {
            log.error("AESEncoder keyCheck key length error. length can't mod 16");
            throw new EasySocketException("key length error. length can't mod 16");
        }
    }
}
