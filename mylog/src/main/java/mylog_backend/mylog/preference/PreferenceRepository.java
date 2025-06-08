package mylog_backend.mylog.preference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Integer> {

    /**
     * userId로 해당 사용자의 태그들을 찾아와 List로 사용
     * @param userId : 사용자 id
     * @return : List로 묶은 태그들
     */
    List<Preference> findByUserId(Long userId);

}
