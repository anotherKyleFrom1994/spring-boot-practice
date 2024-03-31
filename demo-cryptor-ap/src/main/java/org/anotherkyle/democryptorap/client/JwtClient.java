package org.anotherkyle.democryptorap.client;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.anotherkyle.commonlib.ApplicationStatus;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.anotherkyle.democryptorap.common.KeyStoreInitializer;
import org.anotherkyle.democryptorap.common.cryptor.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.util.Map;

@Service
@DependsOn("connectionFactoryInitializer")
@Slf4j
public class JwtClient {
    private JWTUtil jwtUtil;

    @Autowired
    public JwtClient(KeyStoreInitializer keyStoreInitializer) {
        Mono.just(keyStoreInitializer)
                .flatMap(ksi -> {
                    try {
                        if (ksi.getKeyStore() != null) {
                            return Mono.just(JWTUtil.getInstance(new ByteArrayInputStream(ksi.getKeyStore().getData()), "pwd"));
                        } else {
                            log.debug("No keystore record is found");
                            return Mono.empty();
                        }
                    } catch (Exception e) {
                        return Mono.error(ApplicationException.initWithSuppressed(ApplicationStatus.INTERNAL_SERVER_ERROR, e));
                    }
                })
                .repeatWhenEmpty(longFlux -> Flux.just(5L))
                .subscribe(ju -> jwtUtil = ju);
    }

    public Mono<Boolean> validateToken(String authToken) {
        return Mono.fromCallable(() -> jwtUtil.validateToken(authToken))
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.INVALID_LICENSE, e));
    }

    public Mono<Boolean> extractClaim(String authToken, String claimName) {
        return Mono.fromCallable(() -> jwtUtil.extractClaim(authToken, c -> c.get(claimName, Boolean.class)))
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.ILLEGAL_JWT_CLAIM, e));
    }

    public Mono<Claims> extractAllClaims(String authToken) {
        return Mono.fromCallable(() -> jwtUtil.extractAllClaims(authToken))
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.ILLEGAL_JWT_CLAIM, e));
    }

    public Mono<String> generateToken(String subject, Map<String, Object> claims) {
        return Mono.just(jwtUtil.generateTokenRS256(subject, claims))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> ApplicationException.initWithSuppressed(ApplicationStatus.GENERATE_JWT_FAILED, e));
    }
}
