package mylog_backend.mylog.user;

import jakarta.persistence.*;
import lombok.*;
import mylog_backend.mylog.preference.Preference;

import java.util.ArrayList;
import java.util.List;


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


    // 연관관계

    /** 1. 회원 -> 취향 태그
     * 회원 : 태그 = 1:N
     */
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @Builder.Default
    private List<Preference> preferences = new ArrayList<>();
    // 취향 추가 메서드
    public void addPreference(Preference preference) {
        preferences.add(preference); // 취향 추가 메서드
        preference.setUser(this); // 회원을 세팅해줌
    }


}
