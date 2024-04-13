package org.anotherkyle.commonlib.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class RemoteApiCallException extends RuntimeException {
    private final JsonNode response;
    private HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;

    public RemoteApiCallException(JsonNode response, HttpStatusCode status) {
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
