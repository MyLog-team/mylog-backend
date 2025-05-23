package mylog_backend.mylog.prerence;

import mylog_backend.mylog.preference.Preference;
import mylog_backend.mylog.preference.PreferenceRepository;
import mylog_backend.mylog.preference.PreferenceRequest;
import mylog_backend.mylog.preference.PreferenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class PreferenceServiceTest {

    @Autowired
    private PreferenceService preferenceService;
    
    @Autowired
    private PreferenceRepository preferenceRepository;

    @Test
    @DisplayName("선호도 태그 저장 테스트")
    void savePreferences() {
        // given
        PreferenceRequest request = PreferenceRequest.builder()
                .tag1("운동")
                .tag2("독서")
                .build();

        // when
        preferenceService.savePreferences(request);

        // then
        List<Preference> savedPreferences = preferenceRepository.findAll();
        
        assertThat(savedPreferences).hasSize(2);
        assertThat(savedPreferences)
                .extracting("tag")
                .containsExactlyInAnyOrder("운동", "독서");
    }
}