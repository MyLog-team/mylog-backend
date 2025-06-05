package mylog_backend.mylog.memo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository 상속받는 MemoRepository 생성
// 자동으로 구현체가 만들어져 MemoRepository 구현체가 사용됨
@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    /**
     * isVisible = VISIBLE 메모만 필터링
     * 메모를 생성한 유저가 맞는지 판별하기 위해 매개변수 추가
     * @param isVisible : 보이는지 여부
     * @return : VISIBLE 메모를 모두 가져옴
     */
    List<Memo> findByUserIdAndIsVisible(Long userId, IsVisible isVisible);

}
