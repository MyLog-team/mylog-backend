package mylog_backend.mylog.preference;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "preference")
public class Preference {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String tag;

    // 생성자, getter, setter, builder 등
    @Builder
    public Preference(String tag) {
        this.tag = tag;
    }


}
