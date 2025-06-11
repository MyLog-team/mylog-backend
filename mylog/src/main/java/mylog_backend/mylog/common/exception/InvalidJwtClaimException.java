package mylog_backend.mylog.common.exception;

public class InvalidJwtClaimException extends RuntimeException {
    public InvalidJwtClaimException(String message) {
        super(message);
    }
}
