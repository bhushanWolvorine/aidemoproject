package com.aidemoproject.constants;

import java.util.Map;

public class ApiEndpoints {
    public static final Map<String, String> ENDPOINTS = Map.of(
        "HEALTH", "/health",
        "PAYMENT_INTENT", "/api/v1/payments/intent",
        "SEND_OTP", "/api/v1/payments/otp/send",
        "EXECUTE_PAYMENT", "/api/v1/payments/execute",
        "GET_TRANSACTION", "/api/v1/transactions/{session_id}"
    );
}