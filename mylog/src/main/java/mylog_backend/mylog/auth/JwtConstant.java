package mylog_backend.mylog.auth;

public class JwtConstant {
    public static final String GRANT_TYPE = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일
}