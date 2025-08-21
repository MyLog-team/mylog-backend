package mylog_backend.mylog.user;

import jakarta.persistence.*;
import lombok.*;
import mylog_backend.mylog.diary.Diary;
import mylog_backend.mylog.memo.Memo;
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

    @Column(nullable = false, columnDefinition = "VARCHAR(50)", unique = true)
    private String loginId;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)", unique = true)
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
    /**
     * 1. 회원 -> 취향 태그
     * 회원 : 태그 = 1:N
     */
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Preference> preferences = new ArrayList<>();

    //     취향 추가 메서드
    public void addPreference(Preference preference) {
        preferences.add(preference); // 취향 추가 메서드
        preference.setUser(this); // 회원을 세팅해줌
    }


    /**
     * 2. 회원 <-> 메모
     * 회원 : 메모 = 1:N
     */
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Memo> memos = new ArrayList<>();

    // 메모 작성 메서드
    public void addMemo(Memo memo) {
        memo.setUser(this); // this = user 엔티티를 가리킴
        memos.add(memo);
    }
    // 메모 논리적 삭제 메서드
    public void removeMemo(Memo memo) {
        if (memo != null) {
            memo.checkedMemo();  // 메모 상태를 논리 삭제 상태로 변경
        }
    }
    // 메모 목록 조회 메서드
    public List<Memo> getMemos() {
        return memos;
    }
    // 단일 메모 조회 메서드
    public Memo getMemo(Long memoId) {
        return memos.stream()
                .filter(m -> m.getId().equals(memoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 없습니다."));
    }


    /**
     * 3. 일기 <-> 사용자
     * 회원 : 일기 = 1:N
     */
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Diary> diaries = new ArrayList<>();
    // 일기 생성 메서드
    public void addDiary(Diary diary) {
        diary.setUser(this);
        diaries.add(diary);
    }
    // 메모 목록 조회 메서드
    public List<Diary> getDiaries() {
        return diaries;
    }
    // 단일 메모 조회 메서드
    public Memo getDiary(Long diaryId) {
        return memos.stream()
                .filter(m -> m.getId().equals(diaryId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 없습니다."));
    }


}
