package net.ajcloud.wansview.support.core.cipher;

import android.util.Base64;

import net.ajcloud.wansview.support.jnacl.NaCl;
import net.ajcloud.wansview.support.tools.WLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static net.ajcloud.wansview.support.jnacl.curve25519xsalsa20poly1305.crypto_secretbox_NONCEBYTES;

/**
 * Created by mamengchao on 2018/05/23.
 * 加解密
 */
public class CipherUtil {

    private static String TAG = "CipherUtil";
    private static int SALT_LENGTH = 12;
    public static String PUBLICKEY = "0cba66066896ffb51e92bc3c36ffa627c2493770d9b0b4368a2466c801b0184e";
    public static String PRIVATEKEY = "176970653848be5242059e2308dfa30245b93a13befd2ebd09f09b971273b728";

    //uac

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

    public static String naclEncode(String text, String privateKey, String publicKey, byte[] nonce) {
        String encodeMsgContent = null;
        try {
            byte[] encryptPrivateKey = Base64.decode(privateKey, Base64.NO_WRAP);
            byte[] encryptPublicKey = Base64.decode(publicKey, Base64.NO_WRAP);
            byte[] ciphertext = NaCl.encrypt(text.getBytes(), nonce, encryptPublicKey, encryptPrivateKey);
            WLog.d("UserApiUnit", "text-string:" + text);
            WLog.d("UserApiUnit", "text-byte:" + Arrays.toString(text.getBytes()));
            WLog.d("UserApiUnit", "text-nacl-1:" + Arrays.toString(ciphertext));
            ciphertext = clearZero(ciphertext);
            WLog.d("UserApiUnit", "text-nacl-2:" + Arrays.toString(ciphertext));
            encodeMsgContent = new String(Base64.encode(ciphertext, Base64.NO_WRAP), "UTF-8");
            WLog.d("UserApiUnit", "text-nacl-base64:" + encodeMsgContent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return encodeMsgContent;
        }
    }

    private static byte[] clearZero(byte[] oldArr) {
        List<Byte> list = new ArrayList<>();
        boolean isBefore = true;
        for (int i = 0; i < oldArr.length; i++) {
            if (i == 0) {
                isBefore = oldArr[i] == 0;
            }
            if (isBefore) {
                if (oldArr[i] != 0) {
                    list.add(oldArr[i]);
                    isBefore = false;
                }
            } else {
                list.add(oldArr[i]);
            }
        }
        byte newArr[] = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArr[i] = list.get(i);
        }
        return newArr;
    }

    public static String naclDecode(String text, String privateKey, String publicKey, byte[] nonce) {
        String decodeMsgContent = null;
        try {
            byte[] decryptPrivateKey = Base64.decode(privateKey, Base64.NO_WRAP);
            byte[] decryptPublicKey = Base64.decode(publicKey, Base64.NO_WRAP);
            byte[] decryptValue = Base64.decode(text, Base64.NO_WRAP);
            decodeMsgContent = new String(NaCl.decrypt(decryptValue, nonce, decryptPublicKey, decryptPrivateKey), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return decodeMsgContent;
        }
    }

    //local
    public static String md5Encode(String strIN, String salt) {
        String msg = strIN + salt;
        MessageDigest alg;
        try {
            alg = MessageDigest.getInstance("MD5");
            alg.update(msg.getBytes());
            byte[] bytes = alg.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String temp = Integer.toHexString(0xFF & bytes[i]);
                if (temp.length() == 1) {
                    hexString.append("0");
                    hexString.append(temp);
                } else if (temp.length() == 2) {
                    hexString.append(temp);
                } else {
                    WLog.w("MD5Util", "error str:" + msg);
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            WLog.e(TAG, e.getMessage());
        }
        return msg;
    }

    public static String getRandomSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltByte = new byte[crypto_secretbox_NONCEBYTES];
        secureRandom.nextBytes(saltByte);
        return Base64.encodeToString(saltByte, Base64.NO_WRAP);
    }

    public static String naclEncodeLocal(String text, String salt) {
        try {
            byte[] publicKey = PUBLICKEY.getBytes();
            byte[] privateKey = PRIVATEKEY.getBytes();
            byte[] nonce = Base64.decode(salt, Base64.NO_WRAP);
//            return new String(NaCl.encrypt(text.getBytes(), nonce, publicKey, privateKey));
            return new String(Base64.encode(NaCl.encrypt(text.getBytes(), nonce, publicKey, privateKey), Base64.NO_WRAP), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String naclDecodeLocal(String text, String salt) {
        try {
            byte[] publicKey = PUBLICKEY.getBytes();
            byte[] privateKey = PRIVATEKEY.getBytes();
            byte[] nonce = Base64.decode(salt, Base64.NO_WRAP);
            return new String(NaCl.decrypt(Base64.decode(text, Base64.NO_WRAP), nonce, publicKey, privateKey), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
