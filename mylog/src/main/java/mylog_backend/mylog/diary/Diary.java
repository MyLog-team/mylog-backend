package mylog_backend.mylog.diary;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import mylog_backend.mylog.common.domain.DateEntity;
import mylog_backend.mylog.user.User;

@Entity // JPA 사용
@Getter
@Builder // 생성자에 빌더패턴 적용
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 하이버네이트가 Diary 객체를 리플렉션 방식으로 사용하도록 기본 생성자를 Protected 허용
@Table(name = "diary")
public class Diary extends DateEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String dairyTitle;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String dairyContent;

    @Column(length = 10)
    @Enumerated(EnumType.STRING) // DB에 저장시 String(varchar)타입으로 저장됨
    private Feeling feeling;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IsPublic isPublic = IsPublic.PRIVATE; // 기본값이 PRIVATE

    @Column(nullable = false)
    @Builder.Default
    @Min(value = 0, message = "점수는 0 이상이여야 합니다.") // 점수는 0 ~ 100점 사잇값
    @Max(value = 100, message = "점수는 100 이하여야 합니다.")
    private Integer feelingScore = 0; // 감정과 감정점수를 입력하지 않았을때 기본값은 0


    // created_at, modified_at 필드는 DateEntity에 존재하고, Auditing 기능을 통해 관리

    /**
     * Diary 생성자
     * @param dairyTitle : 일기 제목
     * @param dairyContent : 일기 내용
     * @param feeling : 감정 상태
     * @param feelingScore : 감정 점수
     * @param isPublic : 공개 여부
     */
    public Diary(String dairyTitle, String dairyContent, Feeling feeling, Integer feelingScore, IsPublic isPublic) {
        this.dairyTitle = dairyTitle;
        this.dairyContent = dairyContent;
        this.feeling = feeling;
        this.feelingScore = feelingScore;
        this.isPublic = isPublic;
    }


    /**
     * 사용자 <-> 메모
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 사용자 세팅
    public void setUser(User user) {
        this.user = user;
    }



}
