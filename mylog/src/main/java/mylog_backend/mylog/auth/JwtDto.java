package mylog_backend.mylog.auth;

// 토큰을 생성해서 값을 갖고 있을 DTO
public record JwtDto(
        String accessToken,
        String refreshToken) {

}