package org.anotherkyle.democryptorap.server;

import jakarta.validation.Valid;
import org.anotherkyle.commonlib.BaseController;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.anotherkyle.democryptorap.common.model.view.*;
import org.anotherkyle.democryptorap.server.service.ICryptorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
public class CryptorController extends BaseController {
    private final ICryptorService<String> demoSoftCryptor;
    private final ICryptorService<String> hsmCryptor;

    @Autowired
    public CryptorController(ICryptorService<String> demoSoftCryptor, ICryptorService<String> hsmCryptor) {
        this.demoSoftCryptor = demoSoftCryptor;
        this.hsmCryptor = hsmCryptor;
    }

    @PostMapping(value = "/retrieve_pubky")
    public Mono<RetrievePubKeyResponse> retrieveRSAPubKey(@RequestBody @Valid RetrievePubKeyRequest request) {
        return demoSoftCryptor.retrieveSecretKey(request.getFormat())
                .map(RetrievePubKeyResponse::new)
                .onErrorMap(ApplicationException::new);
    }

    @PostMapping(value = "/encrypt_msg")
    public Mono<CryptorResponse> encryptMessage(@RequestBody @Valid EncryptorRequest request) {
        return demoSoftCryptor.encryptMessage(request.getMessage())
                .map(CryptorResponse::new)
                .onErrorMap(ApplicationException::new);
    }

    @PostMapping(value = "/decrypt_msg")
    public Mono<CryptorResponse> decryptMessage(@RequestBody @Valid DecryptorRequest request) {
        return demoSoftCryptor
                .decryptMessage(request.getMessage(), request.getUseUrlSafeBase64())
                .map(CryptorResponse::new)
                .onErrorMap(ApplicationException::new);
    }

    @PostMapping(value = "/hsm/encrypt_msg")
    public Mono<CryptorResponse> encryptHsmMessage(@RequestBody @Valid EncryptorRequest request) {
        return hsmCryptor.encryptMessage(request.getMessage())
                .map(CryptorResponse::new)
                .onErrorMap(ApplicationException::new);
    }

}
