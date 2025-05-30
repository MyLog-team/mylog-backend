package mylog_backend.mylog.preference;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mylog_backend.mylog.auth.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "사용자 선호태그", description = "사용자의 선호 태그 관련 API")
@RestController
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    /**
     *
     * @param request : 선호 태그를 담은 요청 DTO
     * @param userPrincipal : 연관관계 매핑에 사용, 인증된 사용자 객체를 담고 있음.
     * @return
     */
    @Operation(summary = "선호 태그 입력",description = "사용자 선호 태그를 받아낸다.")
    @PostMapping("/preferences")
    public ResponseEntity<PreferenceResponse> savePreferences(@RequestBody PreferenceRequest request,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 인증된 사용자의 id를 할당
        Long userId = userPrincipal.getId();

        // 아이디와 요청을 이용하여 선호 태그 저장 메서드 호출
        PreferenceResponse response =  preferenceService.savePreferences(userId, request);
        return ResponseEntity.ok(response);
    }
}
