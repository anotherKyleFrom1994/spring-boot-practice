package org.anotherkyle.democryptorap.client;

import lombok.NonNull;
import org.anotherkyle.commonlib.ApplicationStatus;
import org.anotherkyle.commonlib.BaseService;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.anotherkyle.commonlib.exception.RemoteApiCallException;
import org.anotherkyle.commonlib.util.APIUtil;
import org.anotherkyle.commonlib.util.JsonUtil;
import org.anotherkyle.democryptorap.common.model.view.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

public class CryptorClient extends BaseService {

    private final CryptorProperties cryptorProperties;

    public CryptorClient(CryptorProperties cryptorProperties) {
        this.cryptorProperties = cryptorProperties;
    }

    public Mono<RetrievePubKeyResponse> retrievePublicKey(RetrievePubKeyRequest request) {
        log.debug("Start retrievePublicKey");

        return Mono
                .justOrEmpty(cryptorProperties.RETRIEVE_PUBKEY_URL)
                .switchIfEmpty(Mono.error(new ApplicationException(ApplicationStatus.URI_DOES_NOT_EXIST)))
                .flatMap(uri -> APIUtil.processCallRemote(
                                HttpMethod.POST,
                                uri,
                                15,
                                request)
                        .flatMap(resp -> {
                            if (HttpStatus.OK.equals(resp.getStatus()))
                                return Mono.just(JsonUtil.treeToValue(resp.getResponseBody(), RetrievePubKeyResponse.class));
                            return Mono.error(new RemoteApiCallException(resp.getResponseBody(), resp.getStatus()));
                        }))
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.GET_KEY_FAILED, e));
    }

    public Mono<CryptorResponse> encryptData(@NonNull String data, Boolean useUrlsafeBase64) {
        log.debug("Start encrypting data...");

        return Mono
                .justOrEmpty(cryptorProperties.ENCRYPT_MSG_URL)
                .switchIfEmpty(Mono.error(new ApplicationException(ApplicationStatus.URI_DOES_NOT_EXIST)))
                .flatMap(uri -> APIUtil.processCallRemote(
                                HttpMethod.POST,
                                uri,
                                15,
                                new EncryptorRequest(data, useUrlsafeBase64))
                        .flatMap(resp -> {
                            if (HttpStatus.OK.equals(resp.getStatus()))
                                return Mono.just(JsonUtil.treeToValue(resp.getResponseBody(), CryptorResponse.class));
                            return Mono.error(new RemoteApiCallException(resp.getResponseBody(), resp.getStatus()));
                        }))
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.ENCRYPTION_FAILED, e));
    }


    public Mono<CryptorResponse> decryptEnd2EndData(@NonNull String encryptedData, Boolean useUrlsafeBase64) {
        log.debug("Start decrypting image_info and transforming...");

        return Mono
                .justOrEmpty(cryptorProperties.DECRYPT_MSG_URL)
                .switchIfEmpty(Mono.error(new ApplicationException(ApplicationStatus.URI_DOES_NOT_EXIST)))
                .flatMap(uri -> APIUtil.processCallRemote(
                                HttpMethod.POST,
                                uri,
                                15,
                                new DecryptorRequest(encryptedData, useUrlsafeBase64))
                        .flatMap(resp -> {
                            if (HttpStatus.OK.equals(resp.getStatus()))
                                return Mono.just(JsonUtil.treeToValue(resp.getResponseBody(), CryptorResponse.class));
                            return Mono.error(new RemoteApiCallException(resp.getResponseBody(), resp.getStatus()));
                        }))
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.DECRYPTION_FAILED, e));
    }
}
