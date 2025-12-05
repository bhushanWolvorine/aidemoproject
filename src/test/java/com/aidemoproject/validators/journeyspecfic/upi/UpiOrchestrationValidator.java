
package com.aidemoproject.validators.journeyspecfic.upi;

import java.util.ArrayList;
import java.util.List;

import com.aidemoproject.basevalidators.orchestration.OrchestrationIssue;
import com.aidemoproject.basevalidators.orchestration.OrchestrationValidator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UpiOrchestrationValidator implements OrchestrationValidator {

 private static final List<String> REQUIRED_SEQUENCE = List.of(
     "extract_payment_intent",
     "send_otp",
     "verify_otp",
     "execute_payment"
 );

 @Override
 public boolean isValidSequence(List<String> conversationLog, String journeyContext) {
     List<String> actual = extractToolSequence(conversationLog);
     return actual.equals(REQUIRED_SEQUENCE);
 }

 @Override
 public List<String> getActualToolSequence(List<String> conversationLog) {
     return extractToolSequence(conversationLog);
 }

 @Override
 public OrchestrationIssue getIssues(List<String> conversationLog) {
     //here we are getting only messages which have tool call
     List<String> actual = extractToolSequence(conversationLog);

     List<String> missing = new ArrayList<>(REQUIRED_SEQUENCE);
     missing.removeAll(actual);

     List<String> unexpected = new ArrayList<>(actual);
     unexpected.removeAll(REQUIRED_SEQUENCE);

     List<String> issues = new ArrayList<>();
     if (!actual.equals(REQUIRED_SEQUENCE)) {
         issues.add("Sequence mismatch: Expected " + REQUIRED_SEQUENCE + " | Got " + actual);
     }

     return new OrchestrationIssue(missing, unexpected, issues);
 }

 @Override
 public double getOrchestrationScore(List<String> conversationLog) {
     return isValidSequence(conversationLog, null) ? 1.0 : 0.0;
 }

 @Override
 public String getJourneyName() {
     return "UPI Payment";
 }


// public List<String> extractToolSequence(List<String> log) {
//     List<String> tools = new ArrayList<>();
//     for (String msg : log) {
//         // Only process real tool_call messages
//         if (msg.contains("\"type\":\"tool_call\"") && msg.contains("\"name\"")) {
//             String toolName = extractToolName(msg);
//             if (toolName != null && !toolName.isBlank()) {
//                 tools.add(toolName.trim());
//             }
//         }
//     }
//     return tools;
// }


    public List<String> extractToolSequence(List<String> log) {
        List<String> tools = new ArrayList<>();

        for (String msg : log) {
            try {

                JsonObject json = JsonParser.parseString(msg).getAsJsonObject();

                if (json.has("type") && "tool_call".equals(json.get("type").getAsString())
                        && json.has("tool") && json.get("tool").getAsJsonObject().has("name")) {

                    String toolName = json.get("tool").getAsJsonObject().get("name").getAsString();
                    tools.add(toolName.trim());
                }
            } catch (Exception e) {

                continue;
            }
        }
        return tools;
    }

 private String extractToolName(String msg) {
     try {
         int nameIndex = msg.indexOf("\"name\":\"");
         if (nameIndex == -1) return null;
         int start = nameIndex + 8;
         int end = msg.indexOf("\"", start);
         return end > start ? msg.substring(start, end).trim() : null;
     } catch (Exception e) {
         return null;
     }
 }

    public static List<String> getRequiredSequence() {
        return REQUIRED_SEQUENCE;
    }
}