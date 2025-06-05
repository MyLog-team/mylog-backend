package mylog_backend.mylog.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    /**
     * 유저 아이디를 기준으로 모든 일기를 서치
     * @param userId
     * @return
     */
    List<Diary> findAllByUserId(Long userId);
    // spring data jpa가 명명한 네이밍 룰에 의해 쿼리 메서드 기능이 작동
    // 위 메서드는 다음 쿼리문과 같은 기능을 한다.
    // SELECT d FROM Diary d WHERE d.user.id = :userId

}
