package mylog_backend.mylog.chatbot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;



@SpringBootTest
@ActiveProfiles("test")
public class ChatbotServiceTest {

    @Autowired
    private ChatbotService chatbotService;

    // application.yml 또는 application-test.yml의 chatgpt.api-key 값을 주입
    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Test
    void getChatResponse() {

        System.out.println("현재 API 키: " + apiKey);  // 주입 여부 확인용 로그

        // given
        String userInput = "요즘 너무 힘들어... 다 귀찮아..ㅠㅠ";
        String response = chatbotService.getChatResponse(userInput);

        // when & then
        System.out.println("응답: " + response);

        assertNotNull(response);
        assertFalse(response.isBlank());
    }
}
