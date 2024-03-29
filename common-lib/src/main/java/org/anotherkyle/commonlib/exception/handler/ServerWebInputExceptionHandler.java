package org.anotherkyle.commonlib.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.anotherkyle.commonlib.exception.model.ServerWebInputExceptionAttrBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebInputException;

@Slf4j
public class ServerWebInputExceptionHandler {
    public static ResponseEntity<?> handleException(ServerWebInputException exception, ServerHttpRequest request) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ServerWebInputExceptionAttrBuilder
                        .create()
                        .originalRequest(request)
                        .exception(exception)
                        .build());
    }
}
