package org.anotherkyle.commonlib.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.anotherkyle.commonlib.exception.model.ApplicationExceptionAttrBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Slf4j
public class ApplicationExceptionHandler {
    public static ResponseEntity<?> handleException(ApplicationException exception, ServerHttpRequest request) {
        return ResponseEntity
                .status(exception.getApiStatus().getHttpStatus())
                .body(ApplicationExceptionAttrBuilder
                        .create()
                        .originalRequest(request)
                        .exception(exception)
                        .build());
    }
}

