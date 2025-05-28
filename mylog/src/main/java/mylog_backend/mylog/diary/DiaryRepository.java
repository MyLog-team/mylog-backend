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


    /**
     * 유저 아이디를 기준으로 단일 일기 조회
     * @param userId
     * @return
     */
    Diary findByUserId(Long userId);
}
