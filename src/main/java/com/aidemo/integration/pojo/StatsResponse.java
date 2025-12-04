package com.aidemo.integration.pojo;



import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsResponse {
    private double uptime;

    @JsonProperty("activeSessions")
    private int activeSessions;

    @JsonProperty("totalProcessed")
    private int totalProcessed;

    private Failures failures;

    public static class Failures {
        private int hallucination;
        private int ragLie;
        private int fakeSuccess;

        // Getters
        public int getHallucination() { return hallucination; }
        public int getRagLie() { return ragLie; }
        public int getFakeSuccess() { return fakeSuccess; }
    }

    // Getters
    public double getUptime() { return uptime; }
    public int getActiveSessions() { return activeSessions; }
    public int getTotalProcessed() { return totalProcessed; }
    public Failures getFailures() { return failures; }
}