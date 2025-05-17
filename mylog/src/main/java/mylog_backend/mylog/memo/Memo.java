package mylog_backend.mylog.memo;

import jakarta.persistence.*;
import lombok.Getter;
import mylog_backend.mylog.common.domain.DateEntity;

@Entity
@Getter
public class Memo extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memoContent;

    @Enumerated(EnumType.STRING) // ENUM 타입을 String으로 지정
    @Column(nullable = false)
    private IsChecked isChecked;

    // modifiedAt, CreatedAt 필드는 DataEntity에 존재

    // TINYINT(1)을 이용해 0과 1로 표현
    // 0: 논리적 삭제, 1: 공개
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private byte isVisible;


}
