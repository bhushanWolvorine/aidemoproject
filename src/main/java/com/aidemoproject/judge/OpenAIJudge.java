package com.aidemoproject.judge;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.List;

public class OpenAIJudge {

    private final OpenAiService service;

    
    public OpenAIJudge() {
//        String apiKey = System.getenv("OPENAI_API_KEY");
        String apiKey = "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";
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable not set");
        }
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
                .model("gpt-4o")
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .maxTokens(500)
                .temperature(0.0)
                .build();

        ChatCompletionResult result = service.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }
}