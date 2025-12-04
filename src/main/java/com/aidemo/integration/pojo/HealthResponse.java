package com.aidemo.integration.pojo;



import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthResponse {
    private String status;
    private String service;
    private String version;
    private String timestamp;

    @JsonProperty("activeConnections")
    private int activeConnections;

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getActiveConnections() { return activeConnections; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
}