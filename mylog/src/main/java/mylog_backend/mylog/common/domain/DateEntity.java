package mylog_backend.mylog.common.domain;

// 생성일자, 수정일자를 선언한 엔티티
// 메모, 일기 엔티티는 이 엔티티를 상속받아서 사용한다.
// Spring Auditing을 사용하여 자동으로 생성일자와 수정일자를 업데이트한다.

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@EntityListeners(value = {AuditingEntityListener.class})

@Getter
@MappedSuperclass
public abstract class DateEntity {

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate modifiedAt;

}
