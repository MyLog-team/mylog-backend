package mylog_backend.mylog;

import mylog_backend.mylog.auth.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class UserPrincipalTest {
    public static void main(String[] args) {
        // 권한 리스트 생성 (예: ROLE_USER, ROLE_ADMIN)
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // UserPrincipal 객체 생성
        UserPrincipal principal = new UserPrincipal(
                123L,
                "testuser@example.com",
                "mypassword",
                authorities
        );

        // 정보 출력
        System.out.println("ID: " + principal.getId());
        System.out.println("Username: " + principal.getUsername());
        System.out.println("Password: " + principal.getPassword());
        System.out.println("Authorities:");
        principal.getAuthorities().forEach(auth -> System.out.println(" - " + auth.getAuthority()));

        System.out.println("isAccountNonExpired: " + principal.isAccountNonExpired());
        System.out.println("isAccountNonLocked: " + principal.isAccountNonLocked());
        System.out.println("isCredentialsNonExpired: " + principal.isCredentialsNonExpired());
        System.out.println("isEnabled: " + principal.isEnabled());
    }
}
