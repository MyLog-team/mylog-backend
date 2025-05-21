package mylog_backend.mylog.preference;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @PostMapping("/preferences")
    public ResponseEntity<Void> savePreferences(@RequestBody PreferenceRequest request) {
        preferenceService.savePreferences(request);  // ✅ 전체 request 객체 전달
        return ResponseEntity.ok().build();
    }
}
