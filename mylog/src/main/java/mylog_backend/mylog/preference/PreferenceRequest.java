package mylog_backend.mylog.preference;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PreferenceRequest {

    private String tag1;

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
