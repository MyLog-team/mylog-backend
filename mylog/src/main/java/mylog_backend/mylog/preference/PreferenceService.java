package mylog_backend.mylog.preference;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;


    public void savePreferences(PreferenceRequest request) {
        List<Preference> preferences = request.toPreferences();
        preferenceRepository.saveAll(preferences);    }
}
