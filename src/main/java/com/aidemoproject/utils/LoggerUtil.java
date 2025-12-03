package com.aidemoproject.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerUtil {

    private static final Logger log = LoggerFactory.getLogger("ApexLLMTest");

    private LoggerUtil() {
        
    }

    public static void info(String message) {
        log.info("INFO   | {}", message);
    }

    public static void warn(String message) {
        log.warn("WARN   | {}", message);
    }

    public static void error(String message) {
        log.error("ERROR  | {}", message);
    }

    public static void error(String message, Throwable t) {
        log.error("ERROR  | {} | {}", message, t.getMessage(), t);
    }

    public static void debug(String message) {
        log.debug("DEBUG  | {}", message);
    }

    public static void step(String stepName) {
        log.info("STEP   | {}", stepName);
    }

    public static void toolCall(String toolName, String args) {
        log.info("TOOL   | {} → {}", toolName, args);
    }

    public static void judgeVerdict(String verdict) {
        log.info("JUDGE  | {}", verdict.replace("\n", " | "));
    }

    public static void testResult(String testName, boolean passed) {
        String status = passed ? "PASSED" : "FAILED";
        log.info("RESULT | {} → {}", testName, status);
    }

    // Bonus: Attach data as log (forwarded to ReportPortal)
    public static void attachAsLog(String name, String content) {
        log.info("ATTACHMENT | {} | {}", name, content);
    }
}


//public class LoggerUtil {
//
//    private static final Logger log = LoggerFactory.getLogger("LLMTest");
//
//    private LoggerUtil() {
//        // Utility class
//    }
//
//    public static void info(String message) {
//        log.info("INFO   | {}", message);
//    }
//
//    public static void warn(String message) {
//        log.warn("WARN  | {}", message);
//    }
//
//    public static void error(String message) {
//        log.error("ERROR | {}", message);
//    }
//
//    public static void error(String message, Throwable t) {
//        log.error("ERROR | {} | {}", message, t.getMessage(), t);
//    }
//
//    public static void debug(String message) {
//        log.debug("DEBUG | {}", message);
//    }
//
//    public static void step(String stepName) {
//        log.info("STEP  | {}", stepName);
//    }
//
//    public static void toolCall(String toolName, String args) {
//        log.info("TOOL  | {} → {}", toolName, args);
//    }
//
//    public static void judgeVerdict(String verdict) {
//        log.info("JUDGE | {}", verdict.replace("\n", " | "));
//    }
//
//    public static void testResult(String testName, boolean passed) {
//        String status = passed ? "PASSED" : "FAILED";
//        log.info("RESULT| {} → {}", testName, status);
//    }
//}