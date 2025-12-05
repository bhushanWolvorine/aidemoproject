package com.aidemoproject.common.listener;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Simple TestNG listener that produces an Extent Spark (HTML) report.
 *
 * This listener is intentionally self-contained and does not depend on any
 * existing logging / Allure / ReportPortal integrations, so it is safe to
 * enable alongside them. It focuses on:
 * - One report per Maven run
 * - Thread-safe test instance handling
 * - Minimal configuration to keep maintenance overhead small
 */
public class ExtentReportListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    private synchronized ExtentReports getExtent(ITestContext context) {
        if (extent != null) {
            return extent;
        }

        // Base directory: target/extent-reports
        String baseDir = System.getProperty("extent.report.dir",
                context.getOutputDirectory().replace("surefire-reports", "extent-reports"));

        // Timestamped file name for easier GitHub artifact browsing
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = baseDir + File.separator + "ExtentReport_" + timestamp + ".html";

        File reportFile = new File(reportPath);
        // Ensure directories are created even in CI
        reportFile.getParentFile().mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("AI Demo Project - TestNG Report");
        spark.config().setReportName("AI Demo Project - Test Execution");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(spark);
        reports.setSystemInfo("Suite", context.getSuite().getName());
        reports.setSystemInfo("Maven Project", context.getCurrentXmlTest().getName());

        extent = reports;
        return extent;
    }

    /**
     * Expose the current {@link ExtentTest} for the running TestNG method.
     * <p>
     * This is intentionally read-only; tests should typically use the helper
     * log methods below instead of dealing with {@link ExtentTest} directly.
     */
    public static ExtentTest getCurrentTest() {
        return currentTest.get();
    }

    // ---- Convenience helpers for tests ------------------------------------

    public static void logInfo(String message) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }

    public static void logWarning(String message) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            // Some ExtentReports versions may not support WARNING explicitly,
            // so use INFO to avoid enum compatibility issues.
            test.log(Status.INFO, "[WARN] " + message);
        }
    }

    public static void logError(String message, Throwable throwable) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            if (throwable != null) {
                test.log(Status.FAIL, "[ERROR] " + message);
                test.fail(throwable); // captures stack trace
            } else {
                test.log(Status.FAIL, "[ERROR] " + message);
            }
        }
    }

    public static void logJson(String label, String json) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            test.log(Status.INFO, label + ": <pre>" + json + "</pre>");
        }
    }

    @Override
    public void onStart(ITestContext context) {
        getExtent(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        if (extent == null) {
            getExtent(result.getTestContext());
        }
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();

        ExtentTest test = extent.createTest(testName)
                .assignCategory(className);
        currentTest.set(test);
        test.log(Status.INFO, "Test started: " + className + "#" + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            test.pass("Test passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            test.fail(result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            test.skip("Test skipped: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            test.warning("Test failed but is within success percentage");
        }
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }
}


