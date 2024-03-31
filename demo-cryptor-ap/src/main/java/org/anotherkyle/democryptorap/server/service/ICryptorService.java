package org.anotherkyle.democryptorap.server.service;

import org.anotherkyle.democryptorap.common.cryptor.RSAUtil;
import reactor.core.publisher.Mono;

public interface ICryptorService<S> {

    Mono<S> retrieveSecretKey(RSAUtil.Format hex);

    Mono<S> decryptMessage(String msg, boolean urlSafeBase64);

    Mono<S> encryptMessage(String msg);

}
