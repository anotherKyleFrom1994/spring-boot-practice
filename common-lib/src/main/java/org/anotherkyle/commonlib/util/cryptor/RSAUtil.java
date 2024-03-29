package org.anotherkyle.commonlib.util.cryptor;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;


public class RSAUtil {
    private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private static final String PKCS_12 = "PKCS12";
    private static RSAUtil instance = null;

    private final KeyPair keypair;

    public RSAUtil(KeyPair key) {
        keypair = key;
    }

    public static RSAUtil getInstance(InputStream keyStoreData, String keyStorePwd) throws Exception {
        if (instance != null) return instance;
        // load key
        KeyStore ks = KeyStore.getInstance(PKCS_12);

        try (InputStream ignored = keyStoreData) {
            ks.load(keyStoreData, keyStorePwd.toCharArray());
        }

        KeyPair keyPair = getKeyPair(ks, keyStorePwd);
        instance = new RSAUtil(keyPair);
        return instance;
    }

    public static RSAUtil getInstance(String keySeed) throws Exception {
        if (instance != null) return instance;

        // create new RSA key
        SecureRandom random = new SecureRandom();
        random.setSeed(keySeed.getBytes(StandardCharsets.UTF_8));

        KeyPairGenerator keygen = KeyPairGenerator.getInstance(RSA_ECB_PKCS1_PADDING);
        keygen.initialize(2048, random);
        KeyPair keyPair = keygen.generateKeyPair();

        instance = new RSAUtil(keyPair);
        return instance;
    }

    /**
     * Get a new RSA instance without start a new one. Send key generation seed,
     * keystore absolute path, and keystore password to initialize it
     **/
    public static synchronized RSAUtil getInstance(String key_Seed, String keystore_Path, String keystore_Pwd)
            throws Exception {
        if (instance != null) return instance;

        File file = new File(keystore_Path);

        if (file.exists()) {
            getInstance(Files.newInputStream(Paths.get(keystore_Path)), keystore_Pwd);
        } else {
            // create new RSA key
            getInstance(key_Seed);
            saveKeyStoreFile(keystore_Path, keystore_Pwd, instance.keypair);
        }

        return instance;
    }

    public static void saveKeyStoreFile(String keyStorePath, String keystorePwd, KeyPair keyPair) throws Exception {
        // sign a certificate to store private key into keystore
        X500NameBuilder builder = getX500NameBuilder();
        X509v3CertificateBuilder certGen = getX509v3CertificateBuilder(keyPair, builder);

        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .build(keyPair.getPrivate());

        X509Certificate cert = getX509Certificate(certGen, sigGen);

        KeyStore ks = KeyStore.getInstance(PKCS_12);
        ks.load(null, keystorePwd.toCharArray());
        try (FileOutputStream out = new FileOutputStream(keyStorePath)) {
            ks.setKeyEntry("RSA", keyPair.getPrivate(), keyStorePath.toCharArray(),
                    new java.security.cert.Certificate[]{cert});

            ks.store(out, keyStorePath.toCharArray());
        }
    }

    private static X509Certificate getX509Certificate(X509v3CertificateBuilder certGen, ContentSigner sigGen) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        X509Certificate cert = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider())
                .getCertificate(certGen.build(sigGen));
        cert.checkValidity(new Date());
        cert.verify(cert.getPublicKey());
        return cert;
    }

    private static X509v3CertificateBuilder getX509v3CertificateBuilder(KeyPair key_pair, X500NameBuilder builder) {
        long year = 360L * 24 * 60 * 60 * 1000;
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + year);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

        return new JcaX509v3CertificateBuilder(builder.build(), serial, notBefore,
                notAfter, builder.build(), key_pair.getPublic());
    }

    private static X500NameBuilder getX500NameBuilder() {
        X500NameBuilder builder = new X500NameBuilder();
        builder.addRDN(BCStyle.CN, "anotherKylrFrom1994");
        builder.addRDN(BCStyle.C, "TW");
        builder.addRDN(BCStyle.E, "rabbit@at.hole");
        return builder;
    }

    private static KeyPair getKeyPair(KeyStore keyStore, String keyStorePwd) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeySpecException {
        Key key = keyStore.getKey("RSA", keyStorePwd.toCharArray());

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key.getEncoded());
        KeyFactory fact = KeyFactory.getInstance("RSA");

        PrivateKey privKey = fact.generatePrivate(keySpec);
        RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privKey;
        RSAPublicKeySpec pubk = new RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());

        PublicKey pubKey = fact.generatePublic(pubk);

        return new KeyPair(pubKey, privKey);
    }

    private static String trimRsaPubKey(String pem_PublicKey) {
        pem_PublicKey = pem_PublicKey.replace("-----BEGIN PUBLIC KEY-----", "");
        pem_PublicKey = pem_PublicKey.replace("-----END PUBLIC KEY-----", "");
        pem_PublicKey = pem_PublicKey.replace("\r\n", "");
        pem_PublicKey = pem_PublicKey.replace("\n", "");
        return pem_PublicKey;
    }

    public String encryptRSAMsg(byte[] msg, Format format) throws Exception {
        Cipher rsaCip = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
        rsaCip.init(Cipher.ENCRYPT_MODE, keypair.getPublic());
        switch (format) {
            case BASE64_URL_SAFE:
                byte[] encode = Base64.getUrlEncoder().encode(rsaCip.doFinal(msg));
                return new String(encode, StandardCharsets.UTF_8);
            case HEX:
            default:
                return HexUtil.byteToHex(rsaCip.doFinal(msg));
        }
    }

    public byte[] decryptRsaMsg(String msg) throws Exception {
        return decryptRsaMsg(msg, Format.HEX);
    }

    /**
     * Decrypt an RSA encrypted msg
     **/
    public byte[] decryptRsaMsg(String msg, Format format) throws Exception {
        Cipher unwrap = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
        unwrap.init(Cipher.DECRYPT_MODE, keypair.getPrivate());

        byte[] decryptedMsg;
        switch (format) {
            case BASE64_URL_SAFE:
                decryptedMsg = unwrap.doFinal(Base64.getUrlDecoder().decode(msg.getBytes(StandardCharsets.UTF_8)));
                return decryptedMsg;
            case HEX:
            default:
                decryptedMsg = unwrap.doFinal(HexUtil.hexToByte(msg));
                return decryptedMsg;
        }
    }

    public String encryptRsaMsgWithPem(String data, String pem_PublicKey) throws Exception {
        return encryptRsaMsgWithPem(data, pem_PublicKey, Format.HEX);
    }

    /**
     * Encrypt an msg with RSA
     **/
    public String encryptRsaMsgWithPem(String data, String pem_PublicKey, Format format) throws Exception {
        Cipher wrap = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);

        pem_PublicKey = trimRsaPubKey(pem_PublicKey);

        byte[] publicKeyBytes = Base64.getDecoder().decode(pem_PublicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ECB_PKCS1_PADDING);

        wrap.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(x509KeySpec));

        byte[] bytes = wrap.doFinal(data.getBytes(StandardCharsets.UTF_8));
        switch (format) {
            case BASE64_URL_SAFE:
                return Base64.getUrlEncoder().encodeToString(bytes);
            case HEX:
            default:
                return HexUtil.byteToHex(bytes);
        }
    }

    public PublicKey getPublicKeyInstance() {
        return keypair.getPublic();
    }

    public String getPublicKey() throws IOException {
        return getPemPublicKey(true, false);
    }

    public String getDerPublicKey(boolean urlSafe) {
        RSAPublicKey pub = (RSAPublicKey) keypair.getPublic();
        byte[] publicKeyBytes = pub.getEncoded();
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        byte[] publicKeyDer = x509EncodedKeySpec.getEncoded();

        // Convert the DER-encoded public key to a Base64 string
        byte[] encodedBytes = urlSafe ?
                Base64.getUrlEncoder().withoutPadding().encode(publicKeyDer) :
                Base64.getEncoder().withoutPadding().encode(publicKeyDer);

        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public String getHexPublicKey() {
        RSAPublicKey pub = (RSAPublicKey) keypair.getPublic();
        byte[] publicKeyBytes = pub.getEncoded();
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        byte[] publicKeyDer = x509EncodedKeySpec.getEncoded();

        return HexUtil.byteToHex(publicKeyDer);
    }

    public String getPemPublicKey(boolean headers, boolean urlSafe) throws IOException {
        RSAPublicKey pub = (RSAPublicKey) keypair.getPublic();
        byte[] publicKeyBytes = pub.getEncoded();

        StringWriter writer = new StringWriter();

        PemWriter pemWriter = new PemWriter(writer);
        pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKeyBytes));
        pemWriter.flush();
        pemWriter.close();

        String pem_PublicKey = writer.toString();
        if (!headers) pem_PublicKey = trimRsaPubKey(pem_PublicKey);

        if (urlSafe) {
            // Decode the original Base64 string
            byte[] decodedBytes = Base64.getDecoder().decode(pem_PublicKey);
            // Encode the bytes into a URL-safe Base64 string
            byte[] encodedBytes = Base64.getUrlEncoder().withoutPadding().encode(decodedBytes);
            pem_PublicKey = new String(encodedBytes, StandardCharsets.UTF_8);
        }

        return pem_PublicKey;
    }

    protected KeyPair getKeyPair() {
        return this.keypair;
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
