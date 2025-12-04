package com.aidemo.integration.pojo;




public class BalanceResponse {
    private int balance;
    private boolean sufficient;
    private String message;

    // Getters
    public int getBalance() { return balance; }
    public boolean isSufficient() { return sufficient; }
    public String getMessage() { return message; }
}