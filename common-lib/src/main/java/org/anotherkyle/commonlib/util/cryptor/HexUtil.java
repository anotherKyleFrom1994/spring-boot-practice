package org.anotherkyle.commonlib.util.cryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

public class HexUtil {
    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public static String byteToHex(byte[] stream) {
        StringBuilder hexStrBuffer = new StringBuilder();
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

    public static byte[] hexToByte(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2]; // 2 Hex -> 8 bit -> 1 Byte
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        // Each 2 HexStr -> 1 HexInt -> 1 Byte.
        return bytes;
    }

    @Deprecated
    public byte[] fileToBytes(File loadFile) {
        try (FileInputStream fis = new FileInputStream(loadFile)) {
            byte[] fbytes = new byte[(int) loadFile.length()];
            fis.close();
            return fbytes;
        } catch (IOException e) {
            log.error("", e);
            return new byte[]{};
        }
    }

}
