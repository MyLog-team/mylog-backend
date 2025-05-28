package mylog_backend.mylog.preference;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import mylog_backend.mylog.user.User;

import java.util.List;

@Schema(description = "사용자 선호 요청 DTO")
@Getter
public class PreferenceRequest {

    @Schema(description = "사용자 선호 태그1", example = "공부")
    private String tag1;

    @Schema(description = "사용자 선호 태그2", example = "클래식")
    private String tag2;


    /**
     * 선호 태그 요청 DTO
     * @param tag1
     * @param tag2
     * 태그는 2개만 받는다.
     */
    @Builder
    public PreferenceRequest(String tag1, String tag2) {
        this.tag1 = tag1;
        this.tag2 = tag2;
    }

    /**
     * Preference 엔티티로 변환하는 메서드
     * @param user : 사용자 객체를 매개값으로 받아서
     * @return : .user()로 사용자를 세팅후, tag들도 할당
     * Preference 생성자에서 태그를 1개밖에 할당하지 않기에 코드를 2줄 사용해서 값을 할당함
     *
     * Note. user 객체는 2번 할당되지만, 해당 코드에선 영속성이 보장되기 때문에 같은 사용자 객체가 사용된다.
     */
    public List<Preference> toPreferences(User user) {
        return List.of(
                Preference.builder().tag(tag1).user(user).build(),
                Preference.builder().tag(tag2).user(user).build()
        );
    }


}
