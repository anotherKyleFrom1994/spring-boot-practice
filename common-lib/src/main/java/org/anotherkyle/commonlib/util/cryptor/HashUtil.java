package org.anotherkyle.commonlib.util.cryptor;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;

public class HashUtil {
    /**
     * @param bytes Raw binary data to be digested
     * @return Raw hash in binary format
     */
    public static byte[] digestSHA256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
            md.update(bytes);
            return md.digest();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param bytes Encoded binary
     * @return Raw hash in binary format
     */
    public static byte[] digestSHA256(byte[] bytes, Format format) {
        byte[] decode;
        switch (format) {
            case BASE64:
                decode = Base64.getDecoder().decode(bytes);
                break;
            case BASE64_URL_SAFE:
                decode = Base64.getUrlDecoder().decode(bytes);
                break;
            case HEX:
                decode = hexToByte(new String(bytes));
                break;
            default:
                throw new IllegalArgumentException("No such codec format");
        }
        return digestSHA256(decode);
    }

    /**
     * @param text Encoded string
     * @return Encoded hash in string format
     */
    public static String digestSHA256(String text, Format format) {
        byte[] decoded = digestSHA256(text.getBytes(StandardCharsets.UTF_8), format);

        switch (format) {
            case BASE64:
                return new String(Base64.getEncoder().encode(decoded), StandardCharsets.UTF_8);
            case BASE64_URL_SAFE:
                return new String(Base64.getUrlEncoder().encode(decoded), StandardCharsets.UTF_8);
            case HEX:
                return byteToHex(decoded);
            default:
                throw new IllegalArgumentException("No such codec format");
        }
    }

    public static String digestSHA256ToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        byte[] digest = digestSHA256(bytes);
        for (byte b : digest) {
            result.append(String.format("%02x", b)); // convert to hex
        }

        return result.toString();
    }

    private static byte[] hexToByte(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2]; // 2 Hex -> 8 bit -> 1 Byte
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        // Each 2 HexStr -> 1 HexInt -> 1 Byte.
        return bytes;
    }

    private static String byteToHex(byte[] stream) {
        StringBuffer hexStrBuffer = new StringBuffer();
        String tmpStr;
        for (byte b : stream) {
            tmpStr = (Integer.toHexString(b & 0XFF));

            if (tmpStr.length() == 1) {
                hexStrBuffer.append("0").append(tmpStr);
            } else {
                hexStrBuffer.append(tmpStr);
            }
        }
        return hexStrBuffer.toString().toUpperCase(Locale.getDefault());
    }

    @Getter
    public enum Format {

        HEX("hex"), BASE64("base64"), BASE64_URL_SAFE("base64UrlSafe");

        @JsonValue
        private final String code;

        Format(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }


}
