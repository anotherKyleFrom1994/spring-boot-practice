package org.anotherkyle.commonlib;

import org.anotherkyle.commonlib.log.LogLevel;
import org.springframework.http.HttpStatus;

public interface ApiStatus {
    String getName();

    HttpStatus getHttpStatus();

    String getCode();

    LogLevel getLogLevel();

    String getMessage();

}
