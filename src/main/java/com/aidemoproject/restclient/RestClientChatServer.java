package com.aidemoproject.restclient;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


public class RestClientChatServer {

 private final String baseUrl;
 private final Map<String, String> endpoints;

 // Constructor — loads config + endpoints
 public RestClientChatServer(String configFile, Map<String, String> endpoints) {
     this.baseUrl = loadBaseUrl(configFile);
     this.endpoints = endpoints;
     RestAssured.baseURI = baseUrl;
     System.out.println("RestAssuredClient initialized → Base URL: " + baseUrl);
 }

 private String loadBaseUrl(String configFile) {
     Properties prop = new Properties();
     try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
         if (input == null) {
             throw new RuntimeException("config.properties not found in classpath");
         }
         prop.load(input);
         String url = prop.getProperty("api.host");
         if (url == null || url.isBlank()) {
             throw new RuntimeException("api.host not defined in config.properties");
         }
         return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
     } catch (IOException e) {
         throw new RuntimeException("Failed to load config.properties", e);
     }
 }

 private RequestSpecification baseRequest() {
     return RestAssured.given()
             .contentType(ContentType.JSON)
             .accept(ContentType.JSON)
             .log().ifValidationFails();
 }

 public Response get(String endpointKey, Map<String, ?> pathParams, Map<String, ?> queryParams) {
     String url = endpoints.get(endpointKey);
     if (url == null) throw new IllegalArgumentException("Endpoint not found: " + endpointKey);

     RequestSpecification req = baseRequest();
     if (pathParams != null) req.pathParams(pathParams);
     if (queryParams != null) req.queryParams(queryParams);

     System.out.println("GET → " + url);
     return req.get(url);
 }

// public Response post(String endpointKey, Object body) {
//     String url = endpoints.get(endpoint);
//     if (url == null) throw new IllegalArgumentException("Endpoint not found: " + endpoint);
//
//     System.out.println("POST → " + url);
//     return baseRequest()
//             .body(body)
//             .post(url);
// }
//
// public Response put(String endpoint, Object body) {
//     String url = endpoints.get(endpoint);
//     if (url == null) throw new IllegalArgumentException("Endpoint not found: " + endpoint);
//
//     System.out.println("PUT → " + url);
//     return baseRequest()
//             .body(body)
//             .put(url);
//
// }

 public Response delete(String endpointKey, Map<String, ?> pathParams) {
     String url = endpoints.get(endpointKey);
     if (url == null) throw new IllegalArgumentException("Endpoint not found: " + endpointKey);

     RequestSpecification req = baseRequest();
     if (pathParams != null) req.pathParams(pathParams);

     System.out.println("DELETE → " + url);
     return req.delete(url);
 }
}