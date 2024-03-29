package org.anotherkyle.commonlib.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.anotherkyle.commonlib.exception.model.WebExchangeBindExceptionAttrBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.support.WebExchangeBindException;

@Slf4j
public class WebExchangeBindExceptionHandler {

    public static ResponseEntity<?> handleException(WebExchangeBindException exception, ServerHttpRequest request) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(WebExchangeBindExceptionAttrBuilder
                        .create()
                        .originalRequest(request)
                        .exception(exception)
                        .build());
    }
}
