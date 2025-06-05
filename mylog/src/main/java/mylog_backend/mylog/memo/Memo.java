package mylog_backend.mylog.memo;

import jakarta.persistence.*;
import lombok.*;
import mylog_backend.mylog.common.domain.DateEntity;
import mylog_backend.mylog.user.User;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 매개변수 없이 객체 생성을 막음
//@RequiredArgsConstructor
@Table(name="memo")
public class Memo extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String memoContent;

    @Enumerated(EnumType.STRING) // ENUM 타입을 String으로 지정
    @Column(nullable = false, length = 10)
    @Builder.Default
    private IsChecked isChecked = IsChecked.UNCHECKED; // 기본값 설정

    // modifiedAt, CreatedAt 필드는 DataEntity에 존재
    // 0: 논리적 삭제, 1: 공개
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private IsVisible isVisible = IsVisible.VISIBLE;

    // 메모 생성자(빌더패턴 적용)
    public Memo(String memoContent, IsChecked isChecked, IsVisible isVisible) {
        this.memoContent = memoContent;
        this.isChecked = isChecked;
        this.isVisible = isVisible;
    }

    /**
     * 1. 사용자 <-> 메모
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    // 사용자 세팅 메서드
    public void setUser(User user) {
        this.user = user;
    }




    //--메모 관련 메서드--//
    /**
     * 1. 메모 체크시 논리적 삭제
     */
    public void checkedMemo() {
        this.isChecked = IsChecked.CHECKED;
        this.isVisible = IsVisible.HIDDEN;
    }




}
