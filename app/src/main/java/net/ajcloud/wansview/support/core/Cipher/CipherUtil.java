package net.ajcloud.wansview.support.core.Cipher;

import android.util.Base64;

import com.neilalexander.jnacl.NaCl;

import java.util.Random;

import static com.neilalexander.jnacl.crypto.curve25519xsalsa20poly1305.crypto_secretbox_NONCEBYTES;

/**
 * Created by mamengchao on 2018/05/23.
 * 加解密
 */
public class CipherUtil {

    /**
     * 生成随机nonce
     */
    public static byte[] getNonce() {
        byte[] nonce = new byte[crypto_secretbox_NONCEBYTES];
        Random random = new Random();
        for (int i = 0; i < nonce.length; i++) {
            nonce[i] = (byte) random.nextInt(256);
        }
        return nonce;
    }

    public static String encode(String text, String privateKey, String publicKey, byte[] nonce) {
        String encodeMsgContent = null;
        try {
            byte[] encryptPrivateKey = Base64.decode(privateKey, Base64.NO_WRAP);
            byte[] encryptPublicKey = Base64.decode(publicKey, Base64.NO_WRAP);
            NaCl naCl = new NaCl(encryptPrivateKey, encryptPublicKey);
            byte[] encryptValue = naCl.encrypt(text.getBytes(), nonce);
            encodeMsgContent = new String(Base64.encode(encryptValue, Base64.NO_WRAP), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return encodeMsgContent;
        }
    }

    public static String decode(String text, String privateKey, String publicKey, byte[] nonce) {
        String decodeeMsgContent = null;
        try {
            byte[] decryptPrivateKey = Base64.decode(privateKey, Base64.NO_WRAP);
            byte[] decryptPublicKey = Base64.decode(publicKey, Base64.NO_WRAP);
            NaCl naCl = new NaCl(decryptPrivateKey, decryptPublicKey);
            byte[] decryptValue = Base64.decode(text, Base64.NO_WRAP);
            decodeeMsgContent = new String(naCl.decrypt(decryptValue, nonce), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return decodeeMsgContent;
        }
    }
}
