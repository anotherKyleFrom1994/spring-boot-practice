package org.anotherkyle.commonlib.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.anotherkyle.commonlib.exception.RemoteApiCallException;
import org.springframework.http.ResponseEntity;

@Slf4j
public class RemoteApiCallExceptionHandler {
    public static ResponseEntity<?> handleException(RemoteApiCallException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(exception.getResponse());
    }
}

