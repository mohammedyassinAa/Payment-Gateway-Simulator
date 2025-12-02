package com.pfa.paymentgateway.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for E2E tests with WebDriver setup and teardown.
 * Provides utilities for screenshot capture on failure.
 */
public abstract class BaseE2ETest {

    protected WebDriver driver;
    protected String baseUrl;

    private static final String SCREENSHOTS_DIR = "target/e2e-screenshots";

    protected void setUp() {
        // Setup WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();

        // Check for headless mode (CI environment)
        String headless = System.getProperty("selenium.headless", "false");
        if ("true".equalsIgnoreCase(headless)) {
            options.addArguments("--headless=new");
        }

        // Common options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        // Create driver
        driver = new ChromeDriver(options);

        // Get base URL from system property or default to localhost
        baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080");
    }

    protected void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Capture screenshot on test failure.
     * 
     * @param testName Name of the failing test
     */
    protected void captureScreenshot(String testName) {
        if (driver instanceof TakesScreenshot) {
            try {
                // Create screenshots directory if it doesn't exist
                Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
                if (!Files.exists(screenshotsPath)) {
                    Files.createDirectories(screenshotsPath);
                }

                // Generate filename with timestamp
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String filename = String.format("%s_%s.png", testName, timestamp);

                // Take screenshot
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destPath = screenshotsPath.resolve(filename);
                Files.copy(srcFile.toPath(), destPath);

                System.out.println("Screenshot saved: " + destPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    /**
     * Capture page source on test failure.
     * 
     * @param testName Name of the failing test
     */
    protected void capturePageSource(String testName) {
        try {
            Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsPath)) {
                Files.createDirectories(screenshotsPath);
            }

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("%s_%s.html", testName, timestamp);
            Path destPath = screenshotsPath.resolve(filename);

            Files.writeString(destPath, driver.getPageSource());
            System.out.println("Page source saved: " + destPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to capture page source: " + e.getMessage());
        }
    }
}
