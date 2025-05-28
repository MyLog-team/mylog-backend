package mylog_backend.mylog.memo;

import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@SpringBootTest
@ActiveProfiles("test")
public class MemoServiceIntegrationTest {

    @Autowired
    private MemoService memoService;
    @Autowired
    private MemoRepository memoRepository;
    @Autowired
    private UserRepository userRepository;

    protected User testUser;
    protected MemoRequest memoRequest1;
    protected MemoRequest memoRequest2;
    protected MemoRequest memoRequest3;
    protected MemoResponse memoResponse1;
    protected MemoResponse memoResponse2;
    protected MemoResponse memoResponse3;



    //     테스트용 데이터 세팅
    @BeforeEach
    void setUp() {
        // 테스트 유저
        testUser = User.builder()
                .loginId("testId1234")
                .email("test@example.com")
                .password("encoded-password")
                .userName("테스트유저")
                .build();
        userRepository.save(testUser);

        // 테스트 메모들
        // testUser와 연관 관계 설정
       memoRequest1 = MemoRequest.builder()
                .memoContent("음~메모~")
                .build();
       memoResponse1 = memoService.createMemo(testUser.getId(), memoRequest1);

        // testUser와 연관 관계 설정
       memoRequest2 = MemoRequest.builder()
                .memoContent("음메모~")
                .build();
       memoResponse2 = memoService.createMemo(testUser.getId(), memoRequest2);

       memoRequest3 = MemoRequest.builder()
                .memoContent("음메모")
                .build();
       memoResponse3 = memoService.createMemo(testUser.getId(), memoRequest3);




    }


    // 테스트후 정보 비워줌
    @AfterEach
    void tearDown() {
        userRepository.deleteAll(); // 또는 모든 repository 초기화
    }




    @Test
    @DisplayName("메모 생성 로직 테스트")
    void createMemo() {
        // given

//         1. 테스트용 메모 생성
        MemoRequest memoRequest = MemoRequest.builder()
                .memoContent("음메모~")
                .build();

        // when
        MemoResponse memoResponse = memoService.createMemo(testUser.getId(), memoRequest);


        // then
        // 메모 응답 DTO에서 아이디 확인
        assertThat(memoResponse.getMemoId()).isNotNull();

        // DB에서 저장된 메모 하나 조회
        Memo savedMemo = memoRepository.findById(memoResponse.getMemoId())
        .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        // DB에서 아이디로 찾은 메모가 예상대로인지 확인
        assertThat(savedMemo.getMemoContent()).isEqualTo("음메모~");
        assertThat(savedMemo.getIsChecked()).isEqualTo(IsChecked.UNCHECKED);
        assertThat(savedMemo.getIsVisible()).isEqualTo(IsVisible.VISIBLE);

        // 테스트용 사용자와 연관관계 확인 -> 지연로딩 문제 발생
        // N+1 문제 발생!! 추후에 실서비스에서 문제 발생시 처리 필요...
        // 일단은 사용자 id만 비교해서 같은지만 확인하자
        assertThat(savedMemo.getUser().getId()).isEqualTo(testUser.getId());

    }


    @Test
    @DisplayName("메모 체크시 논리적 삭제가 이루어져 보이지 않는다.")
    void checkedMemo() {
        // given : 테스트용 메모1의 아이디 할당
        Long savedMemoId = memoResponse1.getMemoId();

        // when
        MemoResponse memoResponse = memoService.checkedMemo(testUser.getId(), savedMemoId);


        // then
        Memo updatedMemo = memoRepository.findById(savedMemoId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        assertThat(memoResponse).isNotNull();
        assertThat(memoResponse.getMemoId()).isEqualTo(savedMemoId);
        assertThat(memoResponse.getIsChecked()).isEqualTo(IsChecked.CHECKED); // 응답 DTO에 변경된 상태 확인
        assertThat(memoResponse.getMessage()).isEqualTo("메모가 체크되어 제거되었습니다."); // 서비스에서 반환하는 예상 메시지

    }


    @Test
    @DisplayName("단일 메모 조회 테스트")
    void getMemo() {

        // given
        Memo savedMemo = memoRepository.findById(memoResponse1.getMemoId())
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        // when
        MemoResponse response = memoService.getMemo(testUser.getId(), savedMemo.getId());


        // then
        assertThat(response.getMemoId()).isEqualTo(savedMemo.getId());
        assertThat(response.getMemoContent()).isEqualTo("음~메모~");
        assertThat(response.getIsVisible()).isEqualTo(IsVisible.VISIBLE);

        assertThat(savedMemo.getUser().getId()).isEqualTo(testUser.getId());



    }

}
