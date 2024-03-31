package org.anotherkyle.democryptorap.server.service;

import org.anotherkyle.commonlib.ApplicationStatus;
import org.anotherkyle.commonlib.BaseService;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.anotherkyle.commonlib.util.Base64Util;
import org.anotherkyle.commonlib.util.JsonUtil;
import org.anotherkyle.democryptorap.common.cryptor.AESUtil;
import org.anotherkyle.democryptorap.common.cryptor.RSAUtil;
import org.anotherkyle.democryptorap.common.KeyStoreInitializer;
import org.anotherkyle.democryptorap.common.model.bo.EncryptedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service("demoSoftCryptor")
@Profile("!test")
@Order(1000)
public class DemoSoftCryptor extends BaseService implements ICryptorService<String> {

    private RSAUtil cryptorInstance;

    @Autowired
    public DemoSoftCryptor(KeyStoreInitializer keyStoreInitializer) {
        Mono.just(keyStoreInitializer)
                .flatMap(ksi -> {
                    try {
                        if (ksi.getKeyStore() != null) {
                            return Mono.just(RSAUtil.getInstance(new ByteArrayInputStream(ksi.getKeyStore().getData()), "pwd"));
                        } else {
                            log.debug("No keystore record is found");
                            return Mono.empty();
                        }
                    } catch (Exception e) {
                        return Mono.error(ApplicationException.initWithSuppressed(ApplicationStatus.INIT_CRYPTOR_FAILED, e));
                    }
                })
                .repeatWhenEmpty(longFlux -> Flux.just(5L))
                .subscribe(ru -> cryptorInstance = ru);
    }

    @Override
    public Mono<String> retrieveSecretKey(RSAUtil.Format format) {
        log.debug("Start retrieveSecretKy");

        return Mono.justOrEmpty(cryptorInstance)
                .publishOn(Schedulers.boundedElastic())
                .map(inst -> {
                    switch (format) {
                        case HEX:
                            return inst.getHexPublicKey();
                        case BASE64:
                            return inst.getDerPublicKey(false);
                        case BASE64_URL_SAFE:
                            return inst.getDerPublicKey(true);
                        default:
                            throw new ApplicationException("Illegal Key format", ApplicationStatus.INPUT_VALIDATE_FAILED);
                    }
                })
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.GET_KEY_FAILED, e));
    }

    @Override
    public Mono<String> decryptMessage(String msg, boolean urlSafeBase64) {
        log.debug("Start decrypting secret message and transforming...");

        return Mono.fromCallable(() -> {
                    String jsonStr = Base64Util.decodeBase64(msg, urlSafeBase64);
                    return JsonUtil.parseToTargetClass(jsonStr, EncryptedData.class);
                })
                .publishOn(Schedulers.boundedElastic())
                .map(encryptedData -> {
                    try {
                        byte[] keyBytes = cryptorInstance.decryptRsaMsg(encryptedData.getKey(), RSAUtil.Format.BASE64_URL_SAFE);
                        byte[] textBytes = Base64Util.decodeUrlBase64(encryptedData.getData().getBytes(StandardCharsets.UTF_8));
                        return new String(AESUtil.decryptRFC2898(keyBytes, textBytes), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.DECRYPTION_FAILED, e));
    }

    @Override
    public Mono<String> encryptMessage(String msg) {
        log.debug("Start encryptMessage");
        return Mono.justOrEmpty(cryptorInstance)
                .publishOn(Schedulers.boundedElastic())
                .map(encryptedData -> {
                    try {
                        String aesKey = new Date().toString();
                        String encryptedAesKey = cryptorInstance.encryptRSAMsg(aesKey.getBytes(StandardCharsets.UTF_8), RSAUtil.Format.BASE64_URL_SAFE);
                        byte[] encryptedMsg = AESUtil.encryptRFC2898(aesKey.getBytes(StandardCharsets.UTF_8), msg.getBytes(StandardCharsets.UTF_8));

                        EncryptedData n2n = new EncryptedData();
                        n2n.setKey(encryptedAesKey);
                        n2n.setData(new String(Base64Util.encodeUrlBase64(encryptedMsg), StandardCharsets.UTF_8));

                        String n2nStr = JsonUtil.parseToJsonString(n2n);
                        return Base64Util.encodeUrlBase64(n2nStr);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.ENCRYPTION_FAILED, e));
    }

}
