package com.aidemoproject.restclient;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;



public class RestClientChatServer {

    private static final String DEFAULT_BASE_URL = "http://localhost:3000";

    // Zero-argument constructor — perfect for tests
    public RestClientChatServer() {
        RestAssured.baseURI = DEFAULT_BASE_URL;
        System.out.println("RestClientChatServer initialized → " + DEFAULT_BASE_URL);
    }

    // Optional: Allow override (for future CI/CD)
    public RestClientChatServer(String baseUrl) {
        String url = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        RestAssured.baseURI = url;
        System.out.println("RestClientChatServer initialized → " + url);
    }

    private RequestSpecification baseRequest() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().ifValidationFails();
    }

    // Health & Stats
    public Response getHealth() {
        System.out.println("GET → " + RestAssured.baseURI + "/health");
        return baseRequest().get("/health");
    }

    public Response getStats() {
        System.out.println("GET → " + RestAssured.baseURI + "/stats");
        return baseRequest().get("/stats");
    }

    // POST Endpoints
    public Response postBalanceCheck(Map<String, Object> body) {
        System.out.println("POST → " + RestAssured.baseURI + "/balance");
        return baseRequest().body(body).post("/balance");
    }

    public Response postOtpSend(Map<String, Object> body) {
        System.out.println("POST → " + RestAssured.baseURI + "/otp/send");
        return baseRequest().body(body).post("/otp/send");
    }

    public Response postExecutePayment(Map<String, Object> body) {
        System.out.println("POST → " + RestAssured.baseURI + "/payment/execute");
        return baseRequest().body(body).post("/payment/execute");
    }

    public Response postResetStats() {
        System.out.println("POST → " + RestAssured.baseURI + "/admin/reset");
        return baseRequest().post("/admin/reset");
    }
}