package org.anotherkyle.democryptorap.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.anotherkyle.commonlib.exception.RemoteApiCallException;
import org.anotherkyle.commonlib.exception.handler.ApplicationExceptionHandler;
import org.anotherkyle.commonlib.exception.handler.RemoteApiCallExceptionHandler;
import org.anotherkyle.commonlib.exception.handler.ServerWebInputExceptionHandler;
import org.anotherkyle.commonlib.exception.handler.WebExchangeBindExceptionHandler;
import org.anotherkyle.commonlib.exception.model.GenericExceptionAttrBuilder;
import org.anotherkyle.commonlib.util.JsonUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception, ServerHttpRequest request) {
        log.error("- Exception: {}", ExceptionUtils.getStackTrace(exception));

        Throwable rootCause = exception.getCause() != null ? ExceptionUtils.getRootCause(exception) : exception;

        if (rootCause instanceof RemoteApiCallException) {
            return RemoteApiCallExceptionHandler
                    .handleException(((RemoteApiCallException) rootCause));

        } else if (rootCause instanceof ApplicationException) {
            return ApplicationExceptionHandler
                    .handleException(((ApplicationException) rootCause), request);

        } else if (exception instanceof WebExchangeBindException) {
            return WebExchangeBindExceptionHandler
                    .handleException(((WebExchangeBindException) exception), request);

        } else if (exception instanceof ServerWebInputException) {
            return ServerWebInputExceptionHandler
                    .handleException(((ServerWebInputException) exception), request);

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericExceptionAttrBuilder
                            .create()
                            .originalRequest(request)
                            .exception(rootCause)
                            .build());
        }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Error.class)
    public ResponseEntity<?> handleError(Error error, ServerHttpRequest request) {
        log.error("- Error: {}", ExceptionUtils.getStackTrace(error));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericExceptionAttrBuilder
                        .create()
                        .originalRequest(request)
                        .exception(error)
                        .build());
    }

    @RequestMapping(value = "/error")
    public ResponseEntity<ObjectNode> error() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(JsonUtil.objectMapper.createObjectNode().put("message", "not found"));
    }
}
