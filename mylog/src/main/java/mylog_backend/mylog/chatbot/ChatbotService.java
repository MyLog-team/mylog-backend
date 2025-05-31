package mylog_backend.mylog.chatbot;

import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatgptService chatgptService;

    public String getChatResponse(String userInput) {
        String prompt = """
            너는 따뜻하고 공감 잘해주는 고민상담 전문가야.
            상대방의 감정을 먼저 이해해주고, 짧고 현실적인 조언을 해줘.
            지나치게 이성적으로 대하거나 상대방이 상처받을 수 있는 조언은 하지마.
            너무 장황하지 않게 2~3문장으로 이야기해줘.

            사용자의 고민: "%s"
            """.formatted(userInput);

        return chatgptService.sendMessage(prompt);
    }
}