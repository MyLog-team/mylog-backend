package mylog_backend.mylog.preference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Integer> {

    /**
     * 1. userId로 사용자를 찾는다.
     * @param userId : 사용자 id
     * @return
     */
    List<Preference> findByUserId(Long userId);

}
