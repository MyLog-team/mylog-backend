package mylog_backend.mylog.memo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository를 상속받는 MemoRepository 생성
// 자동으로 구현체가 만들어져 MemoRepository 구현체가 사용됨
@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

}
