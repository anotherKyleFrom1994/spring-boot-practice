package org.anotherkyle.commonlib.exception.model;

import org.anotherkyle.commonlib.ApplicationStatus;
import lombok.NonNull;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Date;

public final class GenericExceptionAttrBuilder implements ExceptionAttrBuilder {
    private static ExceptionAttributes exceptionAttributes;

    private GenericExceptionAttrBuilder() {
        super();
    }

    public static GenericExceptionAttrBuilder create() {
        GenericExceptionAttrBuilder.exceptionAttributes = ExceptionAttributes.getInstance();
        GenericExceptionAttrBuilder.exceptionAttributes.setTimestamp(new Date());
        return new GenericExceptionAttrBuilder();
    }

    @Override
    public ExceptionAttributes build() {
        return exceptionAttributes;
    }

    @Override
    public ExceptionAttrBuilder exception(@NonNull Throwable throwable) {
        exceptionAttributes.setMessage(throwable.getMessage());
        exceptionAttributes.setException(throwable.getClass().getName());
        exceptionAttributes.setErrorCode(ApplicationStatus.INTERNAL_SERVER_ERROR.toString());
        return this;
    }

    @Override
    public ExceptionAttrBuilder originalRequest(@NonNull ServerHttpRequest httpRequest) {
        exceptionAttributes.setPath(httpRequest.getPath().value());
        return this;
    }
}
