package com.aidemoproject.judge;

import com.aidemoproject.utils.ConfigUtil;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.List;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class OpenAIJudge {

    private final OpenAiService service;

    public OpenAIJudge() {
        String apiKey = System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable not set");
        }
        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
    }


    public String judge(List<String> conversationLog, String promptFileName) {
        String fullLog = String.join("\n", conversationLog);
        String promptTemplate = loadPrompt(promptFileName);
        String prompt = promptTemplate.formatted(fullLog);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .maxTokens(500)
                .temperature(0.0)
                .build();

        ChatCompletionResult result = service.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }


    public String judge(List<String> conversationLog) {
        return judge(conversationLog, "default-upi-judge.txt");
    }


    private String loadPrompt(String fileName) {
        try {
            var url = getClass().getClassLoader().getResource("judge-prompts/" + fileName);
            if (url == null) {
                throw new IllegalArgumentException("Prompt file not found: judge-prompts/" + fileName +
                        "\nPlace it in src/test/resources/judge-prompts/");
            }
            return Files.readString(Paths.get(url.toURI()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load judge prompt: " + fileName, e);
        }
    }
}