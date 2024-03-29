package org.anotherkyle.commonlib.exception.model;

import lombok.NonNull;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Date;

public class ApplicationExceptionAttrBuilder {
    private static ExceptionAttributes exceptionAttributes;

    private ApplicationExceptionAttrBuilder() {
        super();
    }

    public static ApplicationExceptionAttrBuilder create() {
        exceptionAttributes = ExceptionAttributes.getInstance();
        exceptionAttributes.setTimestamp(new Date());
        return new ApplicationExceptionAttrBuilder();
    }

    public ExceptionAttributes build() {
        return exceptionAttributes;
    }

    public ApplicationExceptionAttrBuilder exception(@NonNull Throwable throwable) {
        exceptionAttributes.setMessage(throwable.getMessage());
        exceptionAttributes.setException(throwable.getClass().getName());
        exceptionAttributes.setErrorCode(((ApplicationException) throwable).getApiStatus().toString());
        return this;
    }

    public ApplicationExceptionAttrBuilder originalRequest(@NonNull ServerHttpRequest httpRequest) {
        exceptionAttributes.setPath(httpRequest.getPath().value());
        return this;
    }
}
