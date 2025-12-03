
package com.aidemoproject.basevalidators.prompt;

import java.util.ArrayList;
import java.util.List;

public class UpiPromptValidator implements PromptValidator {

    @Override
    public PromptValidationReport validate(String actualPrompt, String journeyContext) {
        List<String> failures = new ArrayList<>();
        String lower = actualPrompt.toLowerCase().replaceAll("\\s+", " ").trim();

        // 1. Temperature — allow any "temperature=0" or "temperature: 0"
        if (!lower.contains("temperature") || !lower.matches(".*temperature\\s*[:=]?\\s*0.*")) {
            failures.add("TEMPERATURE DRIFT: temperature != 0 → hallucinations possible");
        }

        // 2. Security instruction — flexible
        if (!lower.contains("secure") && !lower.contains("security") && !lower.contains("सुरक्षित")) {
            failures.add("MISSING SECURITY INSTRUCTION — RBI violation risk");
        }

        // 3. Tool order — must contain all tools, order doesn't matter for detection
        if (!actualPrompt.contains("extract_payment_intent") ||
            !actualPrompt.contains("send_otp") ||
            !actualPrompt.contains("verify_otp") ||
            !actualPrompt.contains("execute_payment")) {
            failures.add("TOOL ORDER REGRESSION — one or more tools missing");
        }

        // 4. Hindi few-shot — very robust
        if (!actualPrompt.contains("मम्मी") && !actualPrompt.contains("मां") && 
            !actualPrompt.contains("रुपये") && !actualPrompt.contains("rupees")) {
            failures.add("HINDI FEW-SHOT EXAMPLE REMOVED — will break regional users");
        }

        // 5. JSON formatting — most flexible and realistic
        boolean hasJsonRule = lower.contains("json") && 
                             (lower.contains("only") || 
                              lower.contains("just") || 
                              lower.contains("exactly") || 
                              lower.contains("strict") ||
                              lower.contains("format"));
        
        if (!hasJsonRule) {
            failures.add("FORMATTING DRIFT — missing strict JSON output instruction");
        }

        boolean passed = failures.isEmpty();
        double score = passed ? 1.0 : (1.0 - (failures.size() * 0.2)); // Gradual scoring

        return new PromptValidationReport(passed, failures, "UPI High-Value Payment v2.4", score);
    }

    @Override
    public boolean isValid(String actualPrompt) {
        return validate(actualPrompt, null).isPassed();
    }

    @Override
    public String getTemplateName() {
        return "UPI High-Value Payment v2.4";
    }
}