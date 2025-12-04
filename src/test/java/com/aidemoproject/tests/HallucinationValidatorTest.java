package com.aidemoproject.tests;



import java.util.ArrayList;
import java.util.List;

import com.aidemoproject.base.ExtentReportListener;
import com.aidemoproject.basevalidators.hallucination.HallucinationValidator;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.base.validator.UpiHallucinationValidator;
import com.aidemoproject.utils.LoggerUtil;

@Listeners({com.epam.reportportal.testng.ReportPortalTestNGListener.class})

public class HallucinationValidatorTest extends BaseTest {

    @Test

    public void testUpiHallucination_CatchesOtpLie() throws Exception {
        String sessionId = sessionId();

        ExtentReportListener.logInfo("Starting UPI hallucination test for OTP lie, sessionId=" + sessionId);

        // Mock implemented
        ExtentReportListener.logInfo("Sending high-value UPI payment request over WebSocket");
        ws.send(sessionId, "Send 5000 rupees to mom", "hi");
        ws.waitFor("OTP", 20); 


        List<String> lyingLog = ws.getConversationLog();
        ExtentReportListener.logJson("Conversation log before injected lie", lyingLog.toString());
        lyingLog.add("{\"type\":\"text\",\"content\":\"मैंने आपके फ़ोन पर OTP भेज दिया है। कृपया कोड बताएं।\"}");

        AgentResponse response = AgentResponse.builder()
            .statusCode(200)
            .body("मैंने OTP भेज दिया है")
            .conversationLog(lyingLog)
            .sessionId(sessionId)
            .userMessage("Send 5000...")
            .journeyType("upi")
            .build();

        ExtentReportListener.logJson("AgentResponse for hallucination check", response.toString());

        HallucinationValidator validator = new UpiHallucinationValidator();
        boolean lied = validator.hasHallucination(response.getConversationLog(), response.getSessionId());

        assert lied : "UPI HALLUCINATION NOT DETECTED!";
        System.out.println("UPI HALLUCINATION → CAUGHT");
        LoggerUtil.info("UPI HALLUCINATION → CAUGHT");
        ExtentReportListener.logInfo("UPI hallucination successfully detected for sessionId=" + sessionId);
    }

//    @Test
//    public void testKycHallucination_CatchesVerificationLie() throws Exception {
//        List<String> journeyLog = new ArrayList<>();
//        String sessionId = sessionId();
//
//        // Mock to do so hardcoding here — so we simulate the lie directly
//        List<String> fakeKycLog = List.of(
//            "{\"type\":\"text\",\"content\":\"Please upload your Aadhaar\"}",
//            "{\"type\":\"text\",\"content\":\"Your Aadhaar has been verified successfully.\"}"  // ← LIE!
//        );
//
//        AgentResponse response = AgentResponse.builder()
//            .statusCode(200)
//            .body("Aadhaar verified")
//            .conversationLog(fakeKycLog)
//            .sessionId(sessionId)
//            .userMessage("Verify Aadhaar")
//            .journeyType("kyc")
//            .build();
//
//        HallucinationValidator validator = new KycHallucinationValidator();
//        boolean lied = validator.hasHallucination(response.getConversationLog(), response.getSessionId());
//
//        assert lied : "KYC HALLUCINATION NOT DETECTED!";
//        LoggerUtil.info("KYC HALLUCINATION → CAUGHT");
//    }
//
//    @Test
//    public void testEmailUpdateHallucination_CatchesEmailChangeLie() throws Exception {
//        List<String> journeyLog = new ArrayList<>();
//        String sessionId = sessionId();
//
//        // mock implementation to do
//        List<String> fakeEmailLog = List.of(
//            "{\"type\":\"text\",\"content\":\"We will send OTP to your email\"}",
//            "{\"type\":\"text\",\"content\":\"Your email has been updated to new@gmail.com\"}"
//        );
//
//        AgentResponse response = AgentResponse.builder()
//            .statusCode(200)
//            .body("Email updated")
//            .conversationLog(fakeEmailLog)
//            .sessionId(sessionId)
//            .userMessage("Change email")
//            .journeyType("email_update")
//            .build();
//
//        HallucinationValidator validator = new EmailUpdateHallucinationValidator();
//        boolean lied = validator.hasHallucination(response.getConversationLog(), response.getSessionId());
//
//        assert lied : "EMAIL HALLUCINATION NOT DETECTED!";
//        LoggerUtil.info("EMAIL HALLUCINATION → CAUGHT");
//    }
}