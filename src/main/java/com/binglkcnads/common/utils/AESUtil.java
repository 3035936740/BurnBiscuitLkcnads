package com.binglkcnads.common.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("all")
/**
 * AES（Advanced Encryption Standard）对称加解密工具
 *
 * ECB：电子密码本（Electronic codebook）
 * CBC：密码分组链接（Cipher-block chaining）
 * GCM： Galois/Counter Mode，一种认证加密（authenticated encryption）
 *
 *
 * AES 密钥长度只能是 16、24 或 32 字节，如果不符合要求则会异常
 *
 * Cipher是线程不安全的 不能使用单例
 *
 * 加密后的byte数组是不能强制转换成字符串的，换言之：字符串和byte数组在这种情况下不是互逆的
 *
 */
public class AESUtil {
    private final static String AES_ALGORITHM = "AES";

    /**
     * Initialization Vector
     */
    private final static String IV = "pojnvfdsa4521jio";
    private final static byte[] IV_BYTE = new byte[] {112, 111, 106, 110, 118, 102, 100, 115, 97, 52, 53, 50, 49, 106, 105, 111};
    /**
     * additional authenticated data
     */
    private final static String ADD = "ejkfes25452kjhdukgse45r";
    private final static byte[] ADD_BYTE = new byte[] {101, 106, 107, 102, 101, 115, 50, 53, 52, 53, 50, 107, 106, 104, 100, 117, 107, 103, 115, 101, 52, 53, 114};

    private interface Cryptography {
        /**
         * 加密
         * @param data
         * @param key AES 密钥长度只能是 16、24 或 32 字节，如果不符合要求则会异常
         * @return
         */
        byte[] encrypt(byte[] data, byte[] key);

        /**
         * 解密
         * @param data
         * @param key AES 密钥长度只能是 16、24 或 32 字节，如果不符合要求则会异常
         * @return
         */
        byte[] decrypt(byte[] data, byte[] key);

        /**
         * 这主要是因为加密后的byte数组是不能强制转换成字符串的，换言之：字符串和byte数组在这种情况下不是互逆的；要避免这种情况，
         * 我们需要做一些修订，可以考虑将二进制数据转换成十六进制表示
         *
         * byte[] arr;
         * String temp = new String(arr);
         * arr = temp.getBytes();
         *
         * 这种方式的转换会导致最后加解密的时候，数组长度会因为不是16的倍数而报错
         */
        default String encrypt(String data, String key) {
            return parseByte2HexStr(encrypt(data.getBytes(), key.getBytes()));
        }
        default String decrypt(String data, String key) {
            return new String(decrypt(parseHexStr2Byte(data), key.getBytes()));
        }
    }

    /**
     * ECB 全称为电子密码本（Electronic codebook），将待加密的数据拆分成块，并对每个块进行独立加密。
     * 由于该模式对每个块进行独立加密，会导致同样的明文块被加密成相同的密文块。
     */
    private static class ECB implements Cryptography {
        private final static String AES_ALGORITHM_PADDING = "AES/ECB/PKCS5Padding";

        public byte[] encrypt(byte[] data, byte[] key) {
            try {
                Cipher ECBCipher = Cipher.getInstance(AES_ALGORITHM_PADDING);
                ECBCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM));
                byte[] result = ECBCipher.doFinal(data);
                return result;
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] decrypt(byte[] data, byte[] key) {
            try {
                Cipher ECBCipher = Cipher.getInstance(AES_ALGORITHM_PADDING);
                ECBCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM));
                byte[] result = ECBCipher.doFinal(data);
                return result;
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * CBC 全称为密码分组链接（Cipher-block chaining），它的出现解决 ECB 同样的明文块会被加密成相同的密文块的问题。
     * CBC 引入了初始向量的概念（IV，Initialization Vector），第一个明文块先与 IV 进行异或后再加密，后续每个明文块先与前一个密文块进行异或后再加密。
     * 由于 CBC 每个明文块加密都依赖前一个块的加密结果，所以其主要缺点在于加密过程是串行的，无法被并行化。
     */
    private static class CBC implements Cryptography {
        private final static String AES_ALGORITHM_PADDING = "AES/CBC/PKCS5Padding";

        public byte[] encrypt(byte[] data, byte[] key) {
            try {
                Cipher CBCCipher = Cipher.getInstance(AES_ALGORITHM_PADDING);
                CBCCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM), new IvParameterSpec(IV_BYTE));
                byte[] result = CBCCipher.doFinal(data);
                return result;
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] decrypt(byte[] data, byte[] key) {
            try {
                Cipher CBCCipher = Cipher.getInstance(AES_ALGORITHM_PADDING);
                CBCCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM), new IvParameterSpec(IV_BYTE));
                byte[] result = CBCCipher.doFinal(data);
                return result;
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * GCM 的全称是 Galois/Counter Mode，它是一种认证加密（authenticated encryption）算法。它不但提供了加密解密，还提供了数据完整性校验，防止篡改。
     * AES-GCM 模式是目前使用最广泛的模式，可以尝试抓包看一下目前主流的 https 网站，其中大部分都是基于 GCM 模式。下图是使用抓包工具 Charles 查看浏览器访问 https 网站所使用的加密算法：
     */
    private static class GCM implements Cryptography {
        private final static String AES_ALGORITHM_PADDING = "AES/GCM/NoPadding";

        public byte[] encrypt(byte[] data, byte[] key) {
            try {
                Cipher GCMCipher = Cipher.getInstance(AES_ALGORITHM_PADDING);
                GCMCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM), new GCMParameterSpec(128, IV_BYTE));
                GCMCipher.updateAAD(ADD_BYTE);
                byte[] result = GCMCipher.doFinal(data);
                return result;
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] decrypt(byte[] data, byte[] key) {
            try {
                Cipher GCMCipher = Cipher.getInstance(AES_ALGORITHM_PADDING);
                GCMCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES_ALGORITHM), new GCMParameterSpec(128, IV_BYTE));
                GCMCipher.updateAAD(ADD_BYTE);
                byte[] result = GCMCipher.doFinal(data);
                return result;
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public enum Algorithm {
        ECB("ECB", new ECB()),
        CBC("CBC", new CBC()),
        GCM("GCM", new GCM()),
        ;

        private String name;
        private Cryptography cryptography;

        Algorithm(String name, Cryptography cryptography) {
            this.name = name;
            this.cryptography = cryptography;
        }

        public String getName() {
            return name;
        }

        public Cryptography getCryptography() {
            return cryptography;
        }
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    private static final String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    private static final byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static final byte[] encrypt(byte[] data, byte[] key) {
        return Algorithm.ECB.getCryptography().encrypt(data, key);
    }

    public static final String encrypt(String data, String key) {
        return Algorithm.ECB.getCryptography().encrypt(data, key);
    }

    public static final byte[] decrypt(byte[] data, byte[] key) {
        return Algorithm.ECB.getCryptography().decrypt(data, key);
    }

    public static final String decrypt(String data, String key) {
        return Algorithm.ECB.getCryptography().decrypt(data, key);
    }

    public static final byte[] encrypt(byte[] data, byte[] key, Algorithm algorithm) {
        return algorithm.getCryptography().encrypt(data, key);
    }

    public static final String encrypt(String data, String key, Algorithm algorithm) {
        return algorithm.getCryptography().encrypt(data, key);
    }

    public static final byte[] decrypt(byte[] data, byte[] key, Algorithm algorithm) {
        return algorithm.getCryptography().decrypt(data, key);
    }

    public static final String decrypt(String data, String key, Algorithm algorithm) {
        return algorithm.getCryptography().decrypt(data, key);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=====================DEFAULT=====================");
        String phone = "这个是我的电话号码";
        String hello = "Hello world";
        String chinese = "你好啊";
        String longText = "一大串文本……略……";
        //密钥
        String password = "12345678  jhfset99875654";

        //加密
        String s1 = encrypt(phone, password);
        PrintShow(phone,s1);
        //解密
        String s = decrypt(s1, password);
        if (s.equals(phone)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s1 = encrypt(hello, password);
        PrintShow(hello,s1);
        s = decrypt(s1, password);
        if (s.equals(hello)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s1 = encrypt(chinese, password);
        PrintShow(chinese,s1);
        s = decrypt(s1, password);
        if (s.equals(chinese)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s1 = encrypt(longText, password);
        PrintShow(longText,s1);
        s = decrypt(s1, password);
        if (s.equals(longText)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        System.out.println("=====================CBC=====================");

        String s2 = encrypt(hello, password, Algorithm.CBC);
        PrintShow(longText,s2);
        String s3 = decrypt(s2, password, Algorithm.CBC);
        if (s3.equals(hello)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s2 = encrypt(phone, password, Algorithm.CBC);
        PrintShow(phone,s2);
        s3 = decrypt(s2, password, Algorithm.CBC);
        if (s3.equals(phone)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s2 = encrypt(chinese, password, Algorithm.CBC);
        PrintShow(chinese,s2);
        s3 = decrypt(s2, password, Algorithm.CBC);
        if (s3.equals(chinese)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s2 = encrypt(longText, password, Algorithm.CBC);
        PrintShow(longText,s2);
        s3 = decrypt(s2, password, Algorithm.CBC);
        if (s3.equals(longText)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }


        System.out.println("=====================GCM=====================");
        String s4 = encrypt(phone, password, Algorithm.GCM);
        PrintShow(phone,s4);
        String s5 = decrypt(s4, password, Algorithm.GCM);
        if (s5.equals(phone)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s4 = encrypt(hello, password, Algorithm.GCM);
        PrintShow(hello,s4);
        s5 = decrypt(s4, password, Algorithm.GCM);
        if (s5.equals(hello)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s4 = encrypt(chinese, password, Algorithm.GCM);
        PrintShow(chinese,s4);
        s5 = decrypt(s4, password, Algorithm.GCM);
        if (s5.equals(chinese)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }

        s4 = encrypt(longText, password, Algorithm.GCM);
        PrintShow(longText,s4);
        s5 = decrypt(s4, password, Algorithm.GCM);
        if (s5.equals(longText)) {
            System.out.println(s + " 正确");
        } else {
            System.out.println("错了错了");
        }
    }

    private static void PrintShow(final String s1,final String s2){
        System.out.println("原文:" + s1 + "\t秘文:" + s2);
    }
}