package org.anotherkyle.commonlib.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    public static String decodeUrlBase64(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) throw new NullPointerException("Base64 String is null");
        return new String(decodeBase64(base64Str.getBytes(StandardCharsets.UTF_8), true));
    }

    public static String decodeBase64(String base64Str, boolean urlSafe) {
        if (base64Str == null || base64Str.isEmpty()) throw new NullPointerException("Base64 String is null");
        return new String(decodeBase64(base64Str.getBytes(StandardCharsets.UTF_8), urlSafe));
    }

    public static byte[] decodeUrlBase64(byte[] bytesToDecode) {
        return decodeBase64(bytesToDecode, true);
    }

    public static byte[] decodeBase64(byte[] bytesToDecode, boolean urlSafe) {
        try {
            if (urlSafe) return Base64.getUrlDecoder().decode(bytesToDecode);
            else return Base64.getDecoder().decode(bytesToDecode);
        } catch (Exception e) {
            throw new RuntimeException("Decode Base64 failed", e);
        }
    }

    public static String encodeUrlBase64(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) throw new NullPointerException("Base64 String is null");
        return new String(encodeBase64(base64Str.getBytes(StandardCharsets.UTF_8), true, false));
    }

    public static byte[] encodeUrlBase64(byte[] bytesToEncode) {
        return encodeBase64(bytesToEncode, true, false);
    }

    public static byte[] encodeBase64(byte[] bytesEncode, boolean urlSafe, boolean padding) {
        try {
            Base64.Encoder encoder = urlSafe ? Base64.getUrlEncoder() : Base64.getEncoder();
            encoder = padding ? encoder : encoder.withoutPadding();
            return encoder.encode(bytesEncode);
        } catch (Exception e) {
            throw new RuntimeException("Encode Base64 failed", e);
        }
    }

}
