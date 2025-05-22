package mylog_backend.mylog.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long>{

    /**
     * 회원가입 중복 체크용
     * @param loginId : 비교할 아이디
     * @return
     */
    Optional<User> findByLoginId(String loginId);
}
