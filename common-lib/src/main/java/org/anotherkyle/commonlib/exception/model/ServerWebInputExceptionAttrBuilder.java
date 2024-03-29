package org.anotherkyle.commonlib.exception.model;

import lombok.NonNull;
import org.anotherkyle.commonlib.ApplicationStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Date;

public class ServerWebInputExceptionAttrBuilder {
    private static ExceptionAttributes exceptionAttributes;

    private ServerWebInputExceptionAttrBuilder() {
        super();
    }

    public static ServerWebInputExceptionAttrBuilder create() {
        exceptionAttributes = ExceptionAttributes.getInstance();
        exceptionAttributes.setTimestamp(new Date());
        return new ServerWebInputExceptionAttrBuilder();
    }


    public ExceptionAttributes build() {
        return exceptionAttributes;
    }

    public ServerWebInputExceptionAttrBuilder exception(@NonNull Throwable throwable) {
        exceptionAttributes.setMessage(throwable.getMessage());
        exceptionAttributes.setException(throwable.getClass().getName());
        exceptionAttributes.setErrorCode(ApplicationStatus.INPUT_VALIDATE_FAILED.toString());
        return this;
    }

    public ServerWebInputExceptionAttrBuilder originalRequest(@NonNull ServerHttpRequest httpRequest) {
        exceptionAttributes.setPath(httpRequest.getPath().value());
        return this;
    }
}
