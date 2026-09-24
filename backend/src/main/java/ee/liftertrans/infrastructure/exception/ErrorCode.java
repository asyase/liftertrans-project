package ee.liftertrans.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_SEARCH_PARAMETER(HttpStatus.BAD_REQUEST, "Otsingu parameeter on vigane"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Kasutaja ei ole sisse logitud"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Kasutajal puudub ligipääs");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}