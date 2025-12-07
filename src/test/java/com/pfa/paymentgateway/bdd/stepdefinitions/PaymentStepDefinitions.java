package com.pfa.paymentgateway.bdd.stepdefinitions;

import com.pfa.paymentgateway.e2e.pages.PaymentPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber step definitions for Payment scenarios.
 * These steps connect Gherkin scenarios to Selenium actions.
 */
public class PaymentStepDefinitions {

    private WebDriver driver;
    private PaymentPage paymentPage;
    private String baseUrl;
    private static final String SCREENSHOTS_DIR = "target/bdd-screenshots";

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        String headless = System.getProperty("selenium.headless", "false");
        if ("true".equalsIgnoreCase(headless)) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080");
        paymentPage = new PaymentPage(driver);
    }

    @After
    public void tearDown(io.cucumber.java.Scenario scenario) {
        if (scenario.isFailed()) {
            captureScreenshot(scenario.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    private void captureScreenshot(String scenarioName) {
        if (driver instanceof TakesScreenshot) {
            try {
                Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
                if (!Files.exists(screenshotsPath)) {
                    Files.createDirectories(screenshotsPath);
                }

                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String safeName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_");
                String filename = String.format("%s_%s.png", safeName, timestamp);

                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destPath = screenshotsPath.resolve(filename);
                Files.copy(srcFile.toPath(), destPath);

                System.out.println("Screenshot saved: " + destPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    // ==================== Given Steps ====================

    @Given("I am on the payment page")
    public void iAmOnThePaymentPage() {
        paymentPage.navigateTo(baseUrl);
    }

    // ==================== When Steps ====================

    @When("I enter merchant ID {string}")
    public void iEnterMerchantId(String merchantId) {
        paymentPage.enterMerchantId(merchantId);
    }

    @When("I enter amount {string}")
    public void iEnterAmount(String amount) {
        paymentPage.enterAmount(amount);
    }

    @When("I select currency {string}")
    public void iSelectCurrency(String currency) {
        paymentPage.selectCurrency(currency);
    }

    @When("I enter card number {string}")
    public void iEnterCardNumber(String cardNumber) {
        paymentPage.enterCardNumber(cardNumber);
    }

    @When("I enter cardholder name {string}")
    public void iEnterCardholderName(String name) {
        paymentPage.enterCardholderName(name);
    }

    @When("I enter expiry date {string}")
    public void iEnterExpiryDate(String expiryDate) {
        paymentPage.enterExpiryDate(expiryDate);
    }

    @When("I enter CVV {string}")
    public void iEnterCvv(String cvv) {
        paymentPage.enterCvv(cvv);
    }

    @When("I submit the payment")
    public void iSubmitThePayment() {
        paymentPage.submitAndWaitForResult();
    }

    // ==================== Then Steps ====================

    @Then("I should see a success result")
    public void iShouldSeeASuccessResult() {
        assertTrue(paymentPage.isResultSuccess(), 
            "Expected success result but got: " + paymentPage.getStatus());
    }

    @Then("I should see an error result")
    public void iShouldSeeAnErrorResult() {
        assertTrue(paymentPage.isResultError(), 
            "Expected error result but got: " + paymentPage.getStatus());
    }

    @Then("I should see a pending result")
    public void iShouldSeeAPendingResult() {
        assertTrue(paymentPage.isResultPending(), 
            "Expected pending result but got: " + paymentPage.getStatus());
    }

    @Then("the status should be {string}")
    public void theStatusShouldBe(String expectedStatus) {
        assertEquals(expectedStatus, paymentPage.getStatus(),
            "Status mismatch");
    }

    @Then("I should see a transaction ID")
    public void iShouldSeeATransactionId() {
        String transactionId = paymentPage.getTransactionId();
        assertNotNull(transactionId, "Transaction ID should not be null");
        assertFalse(transactionId.isEmpty(), "Transaction ID should not be empty");
    }

    @Then("the message should contain {string}")
    public void theMessageShouldContain(String expectedText) {
        String message = paymentPage.getMessage().toLowerCase();
        assertTrue(message.contains(expectedText.toLowerCase()),
            "Message '" + message + "' should contain '" + expectedText + "'");
    }

    @Then("the result title should contain {string}")
    public void theResultTitleShouldContain(String expectedText) {
        String title = paymentPage.getResultTitle();
        assertTrue(title.contains(expectedText),
            "Result title '" + title + "' should contain '" + expectedText + "'");
    }
}
