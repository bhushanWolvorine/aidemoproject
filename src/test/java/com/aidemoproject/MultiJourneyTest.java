package com.aidemoproject;



import org.testng.annotations.Test;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.base.validator.ValidationReport;

public class MultiJourneyTest extends BaseTest {
//
// @Test
// public void testKycThenPaymentJourney_WithMongoLogging() throws Exception {
//     clearLog(); // Start fresh
//
//     String sessionId = "multi-001";
//
//     // === STEP 1: KYC Journey ===
//     System.out.println("\nSTEP 1: Starting KYC Journey");
//     ws.send(sessionId, "Please update my Aadhaar to 1234-5678-9012", "en");
//     ws.waitFor("upload", 20);
//     ws.send(sessionId, "[Document uploaded]", "en");
//     ws.waitFor("verified", 20);
//
//     // === STEP 2: Payment in same session ===
//     System.out.println("\nSTEP 2: Starting Payment after KYC");
//     ws.send(sessionId, "Now send 8000 rupees to brother", "en");
//     ws.waitFor("OTP", 20);
//     ws.send(sessionId, "123456", "en");
//     ws.waitFor("successful", 20);
//
//     // === FULL VALIDATION USING DEDICATED VALIDATOR ===
//     AgentResponse response = new AgentResponse(
//         200,
//         "payment success",
//         journeyLog,
//         sessionId,
//         "KYC + Payment",
//         "mixed_kyc_payment"
//     );
//
//     ValidationReport report = new KycPaymentComboValidator().verify(response);
//
//     // === ASSERTIONS ===
//     assert report.isPassed() : "Multi-journey failed: " + report.getFailures();
//     assert !report.isCritical() : "Critical issue in multi-journey flow";
//
//     // === LOG TO MONGO — ONLY CRITICAL JOURNEYS ===
//     mongoLogger.logJourney(
//         "testKycThenPaymentJourney",
//         "KYC → High-Value Payment",
//         journeyLog,
//         report.isPassed()
//     );
//
//     System.out.println("MULTI-JOURNEY TEST PASSED — Logged to MongoDB");
// }
//
// @Test
// public void testSimpleBalanceCheck_NoLogging() throws Exception {
//     clearLog();
//
//     ws.send("bal-001", "What's my balance?", "en");
//     String reply = ws.waitFor("balance", 15);
//
//     assert reply.toLowerCase().contains("balance") : "Balance not returned";
//
//     // No mongo logging here — low-value journey
//     System.out.println("Simple test passed — not logged to Mongo (by design)");
// }
}