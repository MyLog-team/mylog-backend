package mylog_backend.mylog.diary;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import mylog_backend.mylog.common.domain.DateEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diary")
public class Diary extends DateEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String dairyTitle;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String dairyContent;

    @Column(nullable = true, columnDefinition = "VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    private Feeling feeling;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IsPublic isPublic = IsPublic.PRIVATE;

    @Column(nullable = true)
    @Builder.Default
    @Min(value = 0, message = "점수는 0 이상이여야 합니다.")
    @Max(value = 100, message = "점수는 100 이하여야 합니다.")
    private Integer feelingScore = 0;


    // created_at, modified_at 필드는 DateEntity에 존재

    // 생성자
//    public Diary(String dairyTitle, String dairyContent, Feeling feeling, byte feelingScore, IsPublic isPublic) {
//        this.dairyTitle = dairyTitle;
//        this.dairyContent = dairyContent;
//        this.feeling = feeling;
//        this.feelingScore = feelingScore;
//        this.isPublic = isPublic;
//    }


}
