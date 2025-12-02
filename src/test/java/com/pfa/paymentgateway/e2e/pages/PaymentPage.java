package com.pfa.paymentgateway.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Payment Form demo page.
 * Follows Page Object Pattern for maintainability.
 */
public class PaymentPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "merchantId")
    private WebElement merchantIdField;

    @FindBy(id = "amount")
    private WebElement amountField;

    @FindBy(id = "currency")
    private WebElement currencySelect;

    @FindBy(id = "cardNumber")
    private WebElement cardNumberField;

    @FindBy(id = "cardholderName")
    private WebElement cardholderNameField;

    @FindBy(id = "expiryDate")
    private WebElement expiryDateField;

    @FindBy(id = "cvv")
    private WebElement cvvField;

    @FindBy(id = "submitBtn")
    private WebElement submitButton;

    @FindBy(id = "result")
    private WebElement resultContainer;

    @FindBy(id = "resultTitle")
    private WebElement resultTitle;

    @FindBy(id = "transactionId")
    private WebElement transactionIdElement;

    @FindBy(id = "status")
    private WebElement statusElement;

    @FindBy(id = "message")
    private WebElement messageElement;

    @FindBy(id = "declineCode")
    private WebElement declineCodeElement;

    @FindBy(id = "threeDsUrl")
    private WebElement threeDsUrlElement;

    @FindBy(id = "loading")
    private WebElement loadingIndicator;

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/demo/payment");
        wait.until(ExpectedConditions.visibilityOf(merchantIdField));
    }

    public void enterMerchantId(String merchantId) {
        merchantIdField.clear();
        merchantIdField.sendKeys(merchantId);
    }

    public void enterAmount(String amount) {
        amountField.clear();
        amountField.sendKeys(amount);
    }

    public void selectCurrency(String currency) {
        Select select = new Select(currencySelect);
        select.selectByValue(currency);
    }

    public void enterCardNumber(String cardNumber) {
        cardNumberField.clear();
        cardNumberField.sendKeys(cardNumber);
    }

    public void enterCardholderName(String name) {
        cardholderNameField.clear();
        cardholderNameField.sendKeys(name);
    }

    public void enterExpiryDate(String expiryDate) {
        expiryDateField.clear();
        expiryDateField.sendKeys(expiryDate);
    }

    public void enterCvv(String cvv) {
        cvvField.clear();
        cvvField.sendKeys(cvv);
    }

    public void submitPayment() {
        submitButton.click();
    }

    public void waitForResult() {
        // Wait for loading to disappear
        wait.until(ExpectedConditions.invisibilityOf(loadingIndicator));
        // Wait for result to appear
        wait.until(ExpectedConditions.visibilityOf(resultContainer));
    }

    public void waitForResultWithTimeout(int seconds) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        longWait.until(ExpectedConditions.invisibilityOf(loadingIndicator));
        longWait.until(ExpectedConditions.visibilityOf(resultContainer));
    }

    public String getResultTitle() {
        return resultTitle.getText();
    }

    public String getTransactionId() {
        return transactionIdElement.getText();
    }

    public String getStatus() {
        return statusElement.getText();
    }

    public String getMessage() {
        return messageElement.getText();
    }

    public String getDeclineCode() {
        return declineCodeElement.getText();
    }

    public String getThreeDsUrl() {
        return threeDsUrlElement.getText();
    }

    public boolean isResultSuccess() {
        return resultContainer.getAttribute("class").contains("success");
    }

    public boolean isResultError() {
        return resultContainer.getAttribute("class").contains("error");
    }

    public boolean isResultPending() {
        return resultContainer.getAttribute("class").contains("pending");
    }

    public boolean isResultVisible() {
        try {
            return resultContainer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoadingVisible() {
        try {
            return loadingIndicator.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fill in all payment details at once.
     */
    public void fillPaymentForm(String merchantId, String amount, String currency,
                                String cardNumber, String cardholderName,
                                String expiryDate, String cvv) {
        enterMerchantId(merchantId);
        enterAmount(amount);
        selectCurrency(currency);
        enterCardNumber(cardNumber);
        enterCardholderName(cardholderName);
        enterExpiryDate(expiryDate);
        enterCvv(cvv);
    }

    /**
     * Submit payment and wait for result.
     */
    public void submitAndWaitForResult() {
        submitPayment();
        waitForResult();
    }

    /**
     * Submit payment and wait for result with custom timeout.
     */
    public void submitAndWaitForResult(int timeoutSeconds) {
        submitPayment();
        waitForResultWithTimeout(timeoutSeconds);
    }
}
