package mylog_backend.mylog.common.exception;

public class JwtInitializationException extends RuntimeException {
    public JwtInitializationException(String message, Throwable cause) {
        super(message);
    }
}
