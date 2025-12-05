package com.aidemoproject.constants;




//public final class CommunicationConstants {
//
//    private CommunicationConstants() {
//        // Private constructor to prevent instantiation
//        throw new UnsupportedOperationException("Constants class should not be instantiated");
//    }
//
//    // ===================================================================
//    // User Messages (Hindi - Natural Conversational Style)
//    // ===================================================================
//    public static final String USER_MESSAGE_HIGH_VALUE = "Send 5000 rupees to mom";
//    public static final String USER_MESSAGE_RAG_FAILURE = "Send 2000 rupees to mom";
//    public static final String USER_MESSAGE_ORCHESTRATION_FAILURE = "Send 3000 rupees to mom";
//    public static final String USER_MESSAGE_HALLUCINATION_TRIGGER = "Send 1000 rupees to mom";
//
//    // ===================================================================
//    // System / Agent Expected Responses
//    // ===================================================================
//    public static final String EXPECTED_OTP_REQUEST = "OTP";
//    public static final String EXPECTED_JOURNEY_END = "\"type\":\"end\"";
//    public static final String SUCCESS_CONFIRMATION_IN_HINDI = "भेज दिया";
//
//    // ===================================================================
//    // Test Data
//    // ===================================================================
//    public static final String VALID_OTP = "123456";
//    public static final String LANGUAGE_HINDI = "hi";
//    public static final String JOURNEY_TYPE_UPI = "upi_payment";
//
//    // ===================================================================
//    // Timeouts (in seconds)
//    // ===================================================================
//    public static final int TIMEOUT_OTP_REQUEST_SECONDS = 20;
//    public static final int TIMEOUT_JOURNEY_COMPLETION_SECONDS = 30;
//
//    // ===================================================================
//    // Prompt Files for AI Judge (src/test/resources/judge-prompts/)
//    // ===================================================================
//    public static final String PROMPT_HIGH_VALUE_PAYMENT = "upi-high-value.txt";
//    public static final String PROMPT_RAG_FAILURE = "rag-failure.txt";
//    public static final String PROMPT_ORCHESTRATION_FAILURE = "orchestration-failure.txt";
//    public static final String PROMPT_HALLUCINATION = "hallucination-basic.txt";
//
//
//    public static final String USER_MESSAGE_RAG_FAILURE_API = "Send 2000 rupees to mom";
//    public static final String PROMPT_RAG_FAILURE_API = "rag-failure.txt";
//
//
//
//}





public final class CommunicationConstants {

    private CommunicationConstants() {
        // Prevent instantiation — utility class
        throw new UnsupportedOperationException("Constants class should not be instantiated");
    }

    // ===================================================================
    // User Messages (Natural Conversational Style)
    // ===================================================================
    public static final String USER_MESSAGE_HIGH_VALUE             = "Send 5000 rupees to mom";
    public static final String USER_MESSAGE_RAG_FAILURE            = "Send 2000 rupees to mom";
    public static final String USER_MESSAGE_ORCHESTRATION_FAILURE  = "Send 3000 rupees to mom";
    public static final String USER_MESSAGE_HALLUCINATION_TRIGGER  = "Send 1000 rupees to mom";

    // ===================================================================
    // Expected Agent Responses (for waitFor())
    // ===================================================================
    public static final String EXPECTED_OTP_REQUEST     = "OTP";
    public static final String EXPECTED_JOURNEY_END     = "\"type\":\"end\"";
    public static final String SUCCESS_CONFIRMATION_IN_HINDI = "भेज दिया";

    // ===================================================================
    // Test Data
    // ===================================================================
    public static final String VALID_OTP       = "123456";
    public static final String LANGUAGE_HINDI  = "hi";
    public static final String JOURNEY_TYPE_UPI = "upi_payment";

    // ===================================================================
    // Timeouts (in seconds)
    // ===================================================================
    public static final int TIMEOUT_OTP_REQUEST_SECONDS           = 20;
    public static final int TIMEOUT_JOURNEY_COMPLETION_SECONDS     = 30;

    // ===================================================================
    // AI Judge Prompt Files (src/test/resources/judge-prompts/)
    // ===================================================================
    public static final String PROMPT_HIGH_VALUE_PAYMENT         = "upi-high-value.txt";
    public static final String PROMPT_RAG_FAILURE                 = "rag-failure.txt";
    public static final String PROMPT_RAG_FAILURE_PASS                 = "demo-always-pass.txt";

    public static final String PROMPT_ORCHESTRATION_FAILURE      = "orchestration-failure.txt";
    public static final String PROMPT_HALLUCINATION               = "hallucination-basic.txt";
    public static final String PROMPT_DEFAULT                    = "default-upi-judge.txt";

    // ===================================================================
    // Hallucination-specific texts
    // ===================================================================
    /** Hindi text used by agent when it lies about sending OTP (for hallucination tests). */
    public static final String HINDI_OTP_LIE_TEXT =
            "मैंने आपके फ़ोन पर OTP भेज दिया है। कृपया कोड बताएं।";

    /** Journey body snippet used in hallucination response. */
    public static final String HINDI_OTP_LIE_BODY =
            "मैंने OTP भेज दिया है";

    /** Generic UPI journey type label used in hallucination tests. */
    public static final String JOURNEY_TYPE_UPI_GENERIC = "upi";

    // ===================================================================
    // RAG / Retrieval-specific constants
    // ===================================================================
    /** Hindi token used in conversation when balance is mentioned (for RAG tests). */
    public static final String HINDI_BALANCE_TOKEN = "बैलेंस";

    /** Retrieval key used to look up balance-related issues. */
    public static final String RAG_KEY_BALANCE = "balance";

    // ===================================================================
    // REST API Triggers (for direct endpoint testing)
    // ===================================================================
    public static final int AMOUNT_TRIGGER_RAG_LIE           = 2000;
    public static final int AMOUNT_TRIGGER_HALLUCINATION     = 1000;
    public static final int AMOUNT_TRIGGER_FAKE_SUCCESS      = 3000;
    public static final int AMOUNT_NORMAL_FLOW               = 5000;

    public static final String EXPECTED_SEND_OTP_TOOL = "\"tool\":{\"name\":\"send_otp\"";
}

