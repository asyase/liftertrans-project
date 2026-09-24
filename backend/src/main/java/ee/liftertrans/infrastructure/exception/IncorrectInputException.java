package ee.liftertrans.infrastructure.exception;

import lombok.Getter;

@Getter
public class IncorrectInputException extends RuntimeException {

    private final String errorCode;

    public IncorrectInputException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}