package org.anotherkyle.commonlib.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class RemoteApiCallException extends RuntimeException {
    private HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
    private final JsonNode response;

    public RemoteApiCallException(JsonNode response, HttpStatus status) {
        this.status = status;
        this.response = response;
    }

    @Override
    public String toString() {
        return "RemoteApiCallException {" +
                "status=" + status +
                ", response=" + response +
                '}';
    }
}
