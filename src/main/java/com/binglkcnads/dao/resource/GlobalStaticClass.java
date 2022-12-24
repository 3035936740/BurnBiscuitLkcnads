package com.binglkcnads.dao.resource;

import com.alibaba.fastjson.JSONObject;
import com.binglkcnads.common.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Value;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

/**
 * 全局用变量
 * @author Bing
 * */
public class GlobalStaticClass {
    @SuppressWarnings("unused")
    public static Map<String, String> s_rsa_key_map = RSAUtil.createKeys(1024);

    @SuppressWarnings("unused")
    public static String getRSAPublicKey(){
        return s_rsa_key_map.get("publicKey");
    }

    @SuppressWarnings("unused")
    public static String getRSAPrivateKey(){
        return s_rsa_key_map.get("privateKey");
    }

    /**
     * 加密
     * @param data 要加密的字符串
     * @return
     * */
    @SuppressWarnings("unused")
    public static String publicEncrypt(String data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return RSAUtil.publicEncrypt(data, getRSAPublicKey());
    }

    /**
     * 解码
     * @param data 要解密的字符串
     * @return
     * */
    @SuppressWarnings("unused")
    public static String privateDecrypt(String data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return RSAUtil.privateDecrypt(data, getRSAPrivateKey());
    }
}
