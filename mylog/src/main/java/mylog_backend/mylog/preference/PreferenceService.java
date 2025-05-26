package mylog_backend.mylog.preference;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;


    public void savePreferences(Long userId, PreferenceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 엔티티를 찾을 수 없습니다."));


        List<Preference> preferences = request.toPreferences(user);

        preferenceRepository.saveAll(preferences);

    }
}
