package org.anotherkyle.democryptorap.common.cryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AESUtil {

    private static final Map<String, Integer> HEX_MAP;

    static {
        HEX_MAP = new HashMap<>();
        HEX_MAP.put("0", 0);
        HEX_MAP.put("1", 1);
        HEX_MAP.put("2", 2);
        HEX_MAP.put("3", 3);
        HEX_MAP.put("4", 4);
        HEX_MAP.put("5", 5);
        HEX_MAP.put("6", 6);
        HEX_MAP.put("7", 7);
        HEX_MAP.put("8", 8);
        HEX_MAP.put("9", 9);
        HEX_MAP.put("a", 10);
        HEX_MAP.put("b", 11);
        HEX_MAP.put("c", 12);
        HEX_MAP.put("d", 13);
        HEX_MAP.put("e", 14);
        HEX_MAP.put("f", 15);
    }

    public static byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public static byte[] decrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public static byte[] decryptRFC2898(byte[] keyBytes, byte[] textBytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        Rfc2898KeyGenUtil keyGenerator;
        keyGenerator = new Rfc2898KeyGenUtil(keyBytes, keyBytes, 1000);

        byte[] bKey = keyGenerator.getBytes(32);
        byte[] bIv = keyGenerator.getBytes(16);

        IvParameterSpec ivSpec = new IvParameterSpec(bIv);
        SecretKeySpec newKey = new SecretKeySpec(bKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public static byte[] encryptRFC2898(byte[] keyBytes, byte[] textBytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        Rfc2898KeyGenUtil keyGenerator;
        keyGenerator = new Rfc2898KeyGenUtil(keyBytes, keyBytes, 1000);

        byte[] bKey = keyGenerator.getBytes(32);
        byte[] bIv = keyGenerator.getBytes(16);

        IvParameterSpec ivSpec = new IvParameterSpec(bIv);
        SecretKeySpec newKey = new SecretKeySpec(bKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public static byte[] encryptedData(String text) throws UnsupportedEncodingException {

        if (0 != text.length() % 2) {
            throw new UnsupportedEncodingException();
        }
        int size = text.length() / 2;
        byte[] result = new byte[size];

        Pattern pattern = Pattern.compile("[a-f0-9]{2}");
        Matcher matcher = pattern.matcher(text);
        int position = 0;

        while (matcher.find()) {
            String temp = matcher.group();
            result[position] = (byte) ((HEX_MAP.get(temp.substring(0, 1)) * 16) + HEX_MAP.get(temp.substring(1, 2)));
            position += 1;
        }
        return result;

    }

}
