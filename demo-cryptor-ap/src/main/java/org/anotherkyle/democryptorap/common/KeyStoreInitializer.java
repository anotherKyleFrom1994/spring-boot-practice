package org.anotherkyle.democryptorap.common;


import org.anotherkyle.democryptorap.common.dao.KeyStoreRepository;
import org.anotherkyle.democryptorap.common.model.vo.db.KeyStore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class KeyStoreInitializer {
    private final KeyStoreRepository keyStoreRepository;
    @Getter
    private KeyStore keyStore;

    public KeyStoreInitializer(KeyStoreRepository keyStoreRepository) {
        this.keyStoreRepository = keyStoreRepository;
    }

    @Scheduled(fixedRate = 5000L)
    public void init() {
        this.keyStore = refreshKeyPair()
                .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(1)))
                .onErrorComplete()
                .block();
    }

    private Mono<KeyStore> refreshKeyPair() {
        return keyStoreRepository.findLatestNRecords(1)
                .switchIfEmpty(Mono.error(new RuntimeException("Set the keystore to the database to save image")))
                .last()
                .doOnNext(ks -> log.debug("keystore exists: {}", ks != null));
    }

}
