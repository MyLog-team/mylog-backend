package mylog_backend.mylog.memo;

import jakarta.persistence.*;
import lombok.*;
import mylog_backend.mylog.common.domain.DateEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 매개변수 없이 객체 생성을 막음
@RequiredArgsConstructor
@Table(name="memo")
public class Memo extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memoContent;

    @Enumerated(EnumType.STRING) // ENUM 타입을 String으로 지정
    @Column(nullable = false)
    private IsChecked isChecked = IsChecked.UNCHECKED; // 기본값 설정

    // modifiedAt, CreatedAt 필드는 DataEntity에 존재

    // TINYINT(1)을 이용해 0과 1로 표현
    // 0: 논리적 삭제, 1: 공개
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private byte isVisible = 1;

    @Builder
    public Memo(Long id, String memoContent, IsChecked isChecked, byte isVisible) {
        this.id = id;
        this.memoContent = memoContent;
        this.isChecked = isChecked;
        this.isVisible = isVisible;
    }




}
