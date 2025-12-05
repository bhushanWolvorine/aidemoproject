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
     *
     */
    public static ExtentTest getCurrentTest() {
        return currentTest.get();
    }



    public static void logInfo(String message) {
        ExtentTest test = currentTest.get();
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }

    public static void logWarning(String message) {
        ExtentTest test = currentTest.get();
        if (test != null) {

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


