package org.anotherkyle.democryptorap.common.cryptor;


import com.fasterxml.jackson.core.type.TypeReference;
import org.anotherkyle.commonlib.util.JsonUtil;

import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.HashMap;

public class WebPackageUtil {
    private static RSAUtil rsaInst = null;

    /**
     * Send key generatiion seed, keystore absolute path, and keystore password to initialize it
     * *
     */
    public WebPackageUtil(String key_Seed, String keystore_Path, String keystore_Pwd) throws Exception {
        rsaInst = RSAUtil.getInstance(key_Seed, keystore_Path, keystore_Pwd);
    }

    public String encrypt(String key, String data) throws Exception {
        if (key.length() < 8) {
            throw new InvalidKeyException("Key length must be 8 or bigger");
        }

        HashMap<String, String> retMsg = new HashMap<>();
        retMsg.put("key", rsaInst.encryptRsaMsgWithPem(key, rsaInst.getPublicKey()));
        retMsg.put("data", HexUtil.byteToHex(AESUtil.encryptRFC2898(key.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8))));

        return JsonUtil.parseToJsonString(retMsg);
    }

    /**
     * Decrypt and web package
     */
    public String decrypt(String enMsg) throws Exception {
        if (enMsg.length() < 520) {
            throw new BadPaddingException("Data length cannot be less than 520");
        }

        HashMap<String, String> dataSet = JsonUtil.parseToTargetClass(enMsg, new TypeReference<>() {
        });

        return new String(AESUtil.decryptRFC2898(rsaInst.decryptRsaMsg(dataSet.get("key")),
                HexUtil.hexToByte(dataSet.get("data"))), StandardCharsets.UTF_8);
    }

    public String getPubKey() throws IOException {
        return rsaInst.getPublicKey();
    }
}
