package ee.liftertrans.infrastructure.exception;

import lombok.Getter;

@Getter
public class TooManyRequestsException extends RuntimeException {

    private final long retryAfterSeconds;

    public TooManyRequestsException(long retryAfterSeconds) {
        super("Päringute piir on täis. Proovi mõne aja pärast uuesti.");
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
