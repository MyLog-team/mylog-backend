package mylog_backend.mylog.preference;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 선호태그", description = "사용자의 선호 태그 관련 API")
@RestController
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @Operation(summary = "선호 태그 입력",description = "사용자 선호 태그를 받아낸다.")
    @PostMapping("/preferences")
    public ResponseEntity<Void> savePreferences(@RequestBody PreferenceRequest request) {
        preferenceService.savePreferences(request);  // ✅ 전체 request 객체 전달
        return ResponseEntity.ok().build();
    }
}
