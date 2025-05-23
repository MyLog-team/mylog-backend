package mylog_backend.mylog.preference;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "사용자 선호 요청 DTO")
@Getter
public class PreferenceRequest {

    @Schema(description = "사용자 선호 태그1", example = "공부")
    private String tag1;

    @Schema(description = "사용자 선호 태그2", example = "클래식")
    private String tag2;


    @Builder
    public PreferenceRequest(String tag1, String tag2) {
        this.tag1 = tag1;
        this.tag2 = tag2;
    }

    public List<Preference> toPreferences() {
        return List.of(
                Preference.builder().tag(tag1).build(),
                Preference.builder().tag(tag2).build()
        );
    }


}
