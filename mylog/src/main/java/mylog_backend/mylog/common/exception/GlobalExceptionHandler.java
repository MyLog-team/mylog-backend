package mylog_backend.mylog.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 오류 처리기
 * 예외를 런타임 예외로 가공하여 프로젝트 전면에서 사용
 */
@Slf4j
@RestControllerAdvice // 모든 @Controller에 적용
public class GlobalExceptionHandler {

    /**
     *     DuplicateLoginIdException(중복 아이디 예외)를 처리하는 메서드
     * @param : DuplicateLoginIdException
     */
    @ExceptionHandler(DuplicateLoginIdException.class)
    // {"error" : "에러 메시지", "message" : "메시지"} 꼴로 반환
    public ResponseEntity<Map<String, String>> handleDuplicateLoginIdException(DuplicateLoginIdException e) {
        log.error("DuplicateLoginIdException 발생: {}", e.getMessage()); // 서버 로그 파일에 예외 발생 사실 기록
        Map<String, String> errorResponse = new HashMap<>(); // 클라이언트에 JSON 형태로 에러 응답 본문을 구성
        errorResponse.put("error", "Duplicate Login ID");
        errorResponse.put("message", e.getMessage());
        // HTTP 409 Conflict 또는 400 Bad Request 반환
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // 또는 HttpStatus.BAD_REQUEST
        // 409 : 데이터가 충돌이 일어났다는 의미, 400 코드보다 이유가 더 명확하다.
        // 400 : 잘못된 요청을 보냈다는 의미
    }

    /**
     * 유저를 찾지 못했을때 예외
     * @param e
     * @return
     */
     @ExceptionHandler(UserNotFoundException.class)
     public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException e) {
         Map<String, String> errorResponse = new HashMap<>();
         errorResponse.put("error", "User Not Found");
         errorResponse.put("message", e.getMessage());
         return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
     }


    /**
     * 잘못된 인증 요청시 발생하는 예외
     * @param e
     * @return
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException e) {
        log.error("BadCredentialsException 발생: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid Credentials");
        errorResponse.put("message", "아이디 또는 비밀번호가 올바르지 않습니다."); // 사용자에게 보여줄 메시지
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 또는 HttpStatus.UNAUTHORIZED
    }


    /**
     * 본인이 생성한 데이터가 아닌데 접근을 시도할때 처리
     * @param e
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("권한 없는 접근 시도: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);  // 401 상태코드
    }


    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleVideoNotFoundException(VideoNotFoundException e) {
        log.error("영상을 찾지 못했습니다: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "VideoNotFound");
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404 상태코드
    }


    /**
     *  일반적인 RuntimeException을 처리하는 핸들러 (최하단에 위치)
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("예상치 못한 런타임 예외 발생: {}", e.getMessage(), e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}



