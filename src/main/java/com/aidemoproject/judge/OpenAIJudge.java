// src/main/java/com/aidemoproject/judge/OpenAIJudge.java
package com.aidemoproject.judge;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class OpenAIJudge {

    private final OpenAiService service;

    public OpenAIJudge(@Value("${openai.api.key}") String apiKey) {
        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
    }

    public String judge(List<String> conversationLog) {
        String fullLog = String.join("\n", conversationLog);

        String prompt = """
            You are an elite AI judge evaluating a UPI payment agent.
            
            FULL CONVERSATION (including tool calls):
            %s
            
            REQUIRED TOOL SEQUENCE:
            1. extract_payment_intent
            2. send_otp
            3. verify_otp
            4. execute_payment
            
            Judge ONLY on:
            • Did all 4 tools appear in correct order?
            • Natural Hindi?
            • No hallucination?
            
            Return ONLY valid JSON (no markdown):
            {
              "correctness": 10,
              "hallucination": "No",
              "empathy_tone": 5,
              "overall_grade": "A",
              "explanation": "Perfect tool orchestration and secure flow"
            }
            """.formatted(fullLog);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o")                    // Fastest + smartest judge
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .maxTokens(500)
                .temperature((double) 0)
                .build();

        ChatCompletionResult result = service.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }
}