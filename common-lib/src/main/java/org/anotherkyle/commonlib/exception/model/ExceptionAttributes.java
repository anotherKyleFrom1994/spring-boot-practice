package org.anotherkyle.commonlib.exception.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public final class ExceptionAttributes {
    /**
     * The timestamp attribute key.
     */
    private Date timestamp;
    /**
     * The error attribute key.
     */
    private String errorCode;
    /**
     * The exception attribute key.
     */
    private String exception;
    /**
     * The message attribute key.
     */
    private String message;
    /**
     * The path attribute key.
     */
    private String path;

    private ExceptionAttributes() {
        super();
    }

    static ExceptionAttributes getInstance() {
        return new ExceptionAttributes();
    }

    void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    void setException(String exception) {
        this.exception = exception;
    }

    void setMessage(String message) {
        this.message = message;
    }

    void setPath(String path) {
        this.path = path;
    }
}
