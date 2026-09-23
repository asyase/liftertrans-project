package ee.liftertrans.infrastructure.exception;

import lombok.Getter;

@Getter


public class UnauthorizedException extends RuntimeException {

    private final String errorCode;

    public UnauthorizedException(String message, String errorCode ){
        super(message);
        this.errorCode=errorCode;
    }
}
