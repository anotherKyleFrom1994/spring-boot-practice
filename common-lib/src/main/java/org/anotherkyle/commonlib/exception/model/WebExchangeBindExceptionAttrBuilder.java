package org.anotherkyle.commonlib.exception.model;

import lombok.NonNull;
import org.anotherkyle.commonlib.ApplicationStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WebExchangeBindExceptionAttrBuilder {
    private static ExceptionAttributes exceptionAttributes;

    private WebExchangeBindExceptionAttrBuilder() {
        super();
    }

    public static WebExchangeBindExceptionAttrBuilder create() {
        exceptionAttributes = ExceptionAttributes.getInstance();
        exceptionAttributes.setTimestamp(new Date());
        return new WebExchangeBindExceptionAttrBuilder();
    }

    private static String extractFieldValidationMsg(WebExchangeBindException exception) {
        List<FieldError> fieldErrors = exception
                .getBindingResult()
                .getFieldErrors();
        return fieldErrors
                .stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));
    }

    public ExceptionAttributes build() {
        return exceptionAttributes;
    }

    public WebExchangeBindExceptionAttrBuilder exception(@NonNull Throwable throwable) {
        exceptionAttributes.setMessage(extractFieldValidationMsg((WebExchangeBindException) throwable));
        exceptionAttributes.setException(throwable.getClass().getName());
        exceptionAttributes.setErrorCode(ApplicationStatus.INPUT_VALIDATE_FAILED.toString());
        return this;
    }

    public WebExchangeBindExceptionAttrBuilder originalRequest(@NonNull ServerHttpRequest httpRequest) {
        exceptionAttributes.setPath(httpRequest.getPath().value());
        return this;
    }
}
