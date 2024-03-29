package org.anotherkyle.commonlib.exception.model;

import lombok.NonNull;
import org.springframework.http.server.reactive.ServerHttpRequest;

public interface ExceptionAttrBuilder {


    ExceptionAttributes build();

    ExceptionAttrBuilder exception(@NonNull Throwable throwable);

    ExceptionAttrBuilder originalRequest(@NonNull ServerHttpRequest httpRequest);
}
