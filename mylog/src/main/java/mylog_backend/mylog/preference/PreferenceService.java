package mylog_backend.mylog.preference;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.user.User;
import mylog_backend.mylog.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;


    /**
     * 사용자의 취향 태그를 저장하는 메서드
     * @param userId : 사용자 id
     * @param request : 사용자가 선호 태그를 입력했을때 가공하는 DTO
     */
    @Transactional // User 엔티티 지연로딩 해결
    public void savePreferences(Long userId, PreferenceRequest request) {
        // DB에 저장된 사용자를 가져옴
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 엔티티를 찾을 수 없습니다."));

        // user 엔티티를 담아서 Preperence 엔티티로 변환
        // user 엔티티도 같이 세팅 -> 연관관계 메서드
        List<Preference> preferences = request.toPreferences(user);

        preferenceRepository.saveAll(preferences);

    }
}
