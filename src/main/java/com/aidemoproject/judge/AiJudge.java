package com.aidemoproject.judge;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AiJudge {

    private final OpenAiService service;

    public AiJudge(OpenAiService service) {
        this.service = service;
    }

    /**
     * Configurable AI Judge — loads prompt from src/test/resources/judge-prompts/
     */
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

    // Fallback for old tests
    public String judge(List<String> conversationLog) {
        return judge(conversationLog, "default-upi-judge.txt");
    }

    /**
     * Loads prompt from classpath (works in IDE + Maven + JAR)
     */
    private String loadPrompt(String fileName) {
        try {
            var url = getClass().getClassLoader().getResource("judge-prompts/" + fileName);
            if (url == null) {
                throw new IllegalArgumentException("Prompt file not found: judge-prompts/" + fileName +
                        "\nCheck src/test/resources/judge-prompts/" + fileName);
            }
            return Files.readString(Paths.get(url.toURI()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load AI judge prompt: " + fileName, e);
        }
    }
}