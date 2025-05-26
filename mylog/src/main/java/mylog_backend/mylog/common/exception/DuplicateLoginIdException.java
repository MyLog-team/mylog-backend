package mylog_backend.mylog.common.exception;

public class DuplicateLoginIdException extends RuntimeException {
    public DuplicateLoginIdException(String message) {
        super(message);
    }
}