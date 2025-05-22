package mylog_backend.mylog.user;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_table")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)", unique = true)
    private String loginId;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)", unique = true)
    private String userName;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String password;


    /**
     * 소셜 로그인 연동시 수정할 필드
     */
    @Column
    private String provider; // 로그인 연동시 사용할 플랫폼

    @Column
    private String email;


    @Column
    private String providerId;


}
