package mylog_backend.mylog.preference;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mylog_backend.mylog.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "preference")
public class Preference {

    @Id @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String tag;

    // 생성자, getter, setter, builder 등
    @Builder
    public Preference(String tag, User user) {
        this.tag = tag;
        this.user = user;
    }

    /** 1.
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }


    }

