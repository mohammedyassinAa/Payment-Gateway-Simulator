package com.pfa.paymentgateway.e2e;

import com.pfa.paymentgateway.e2e.pages.PaymentPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E tests for payment flows using Selenium WebDriver.
 * Tests the complete user journey through the demo UI.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Payment E2E Tests")
class PaymentE2ETest extends BaseE2ETest {

    private PaymentPage paymentPage;

    @BeforeEach
    void setUpTest() {
        setUp();
        paymentPage = new PaymentPage(driver);
    }

    @AfterEach
    void tearDownTest(TestInfo testInfo) {
        if (testInfo.getTags().contains("failed")) {
            captureScreenshot(testInfo.getDisplayName());
            capturePageSource(testInfo.getDisplayName());
        }
        tearDown();
    }

    @Test
    @Order(1)
    @DisplayName("E2E: Approved payment flow")
    void approvedPaymentFlow() {
        try {
            // Navigate to payment page
            paymentPage.navigateTo(baseUrl);

            // Fill payment form with approved card
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "100.00",
                    "EUR",
                    "4242424242424242",
                    "E2E Test User",
                    "1225",
                    "123"
            );

            // Submit and wait for result
            paymentPage.submitAndWaitForResult();

            // Verify success result
            assertTrue(paymentPage.isResultSuccess(), "Result should be success");
            assertEquals("APPROVED", paymentPage.getStatus());
            assertTrue(paymentPage.getResultTitle().contains("Successful"));
            assertFalse(paymentPage.getTransactionId().isEmpty());
        } catch (AssertionError | Exception e) {
            captureScreenshot("approvedPaymentFlow");
            capturePageSource("approvedPaymentFlow");
            throw e;
        }
    }

    @Test
    @Order(2)
    @DisplayName("E2E: Declined payment flow")
    void declinedPaymentFlow() {
        try {
            paymentPage.navigateTo(baseUrl);

            // Fill payment form with declined card
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "50.00",
                    "EUR",
                    "4000000000000002",
                    "E2E Declined User",
                    "1225",
                    "123"
            );

            paymentPage.submitAndWaitForResult();

            // Verify declined result
            assertTrue(paymentPage.isResultError(), "Result should be error");
            assertEquals("DECLINED", paymentPage.getStatus());
            assertTrue(paymentPage.getResultTitle().contains("Failed"));
        } catch (AssertionError | Exception e) {
            captureScreenshot("declinedPaymentFlow");
            capturePageSource("declinedPaymentFlow");
            throw e;
        }
    }

    @Test
    @Order(3)
    @DisplayName("E2E: Fraud suspected payment flow")
    void fraudSuspectedPaymentFlow() {
        try {
            paymentPage.navigateTo(baseUrl);

            // Fill payment form with fraud card
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "200.00",
                    "EUR",
                    "4000000000000069",
                    "E2E Fraud User",
                    "1225",
                    "123"
            );

            paymentPage.submitAndWaitForResult();

            // Verify fraud result
            assertTrue(paymentPage.isResultError(), "Result should be error");
            assertEquals("FRAUD_SUSPECTED", paymentPage.getStatus());
            assertTrue(paymentPage.getMessage().toLowerCase().contains("fraud"));
        } catch (AssertionError | Exception e) {
            captureScreenshot("fraudSuspectedPaymentFlow");
            capturePageSource("fraudSuspectedPaymentFlow");
            throw e;
        }
    }

    @Test
    @Order(4)
    @DisplayName("E2E: 3DS required payment flow")
    void threeDsRequiredPaymentFlow() {
        try {
            paymentPage.navigateTo(baseUrl);

            // Fill payment form with 3DS card
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "150.00",
                    "EUR",
                    "4000000000003220",
                    "E2E 3DS User",
                    "1225",
                    "123"
            );

            paymentPage.submitAndWaitForResult();

            // Verify 3DS result
            assertTrue(paymentPage.isResultPending(), "Result should be pending");
            assertEquals("THREE_DS_REQUIRED", paymentPage.getStatus());
            assertTrue(paymentPage.getResultTitle().contains("3D Secure"));
        } catch (AssertionError | Exception e) {
            captureScreenshot("threeDsRequiredPaymentFlow");
            capturePageSource("threeDsRequiredPaymentFlow");
            throw e;
        }
    }

    @Test
    @Order(5)
    @DisplayName("E2E: High latency payment flow")
    void highLatencyPaymentFlow() {
        try {
            paymentPage.navigateTo(baseUrl);

            // Fill payment form with high latency card (3s delay)
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "75.00",
                    "EUR",
                    "4000000000009999",
                    "E2E Latency User",
                    "1225",
                    "123"
            );

            // Submit payment
            paymentPage.submitPayment();

            // Verify loading indicator appears
            assertTrue(paymentPage.isLoadingVisible(), "Loading indicator should be visible");

            // Wait for result with extended timeout
            paymentPage.waitForResultWithTimeout(15);

            // Should still be approved after delay
            assertTrue(paymentPage.isResultSuccess(), "Result should be success after latency");
            assertEquals("APPROVED", paymentPage.getStatus());
        } catch (AssertionError | Exception e) {
            captureScreenshot("highLatencyPaymentFlow");
            capturePageSource("highLatencyPaymentFlow");
            throw e;
        }
    }

    @Test
    @Order(6)
    @DisplayName("E2E: Error payment flow")
    void errorPaymentFlow() {
        try {
            paymentPage.navigateTo(baseUrl);

            // Fill payment form with error card
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "99.00",
                    "EUR",
                    "4000000000000085",
                    "E2E Error User",
                    "1225",
                    "123"
            );

            paymentPage.submitAndWaitForResult();

            // Verify error result
            assertTrue(paymentPage.isResultError(), "Result should be error");
            assertEquals("ERROR", paymentPage.getStatus());
        } catch (AssertionError | Exception e) {
            captureScreenshot("errorPaymentFlow");
            capturePageSource("errorPaymentFlow");
            throw e;
        }
    }

    @Test
    @Order(7)
    @DisplayName("E2E: Multiple payment attempts")
    void multiplePaymentAttempts() {
        try {
            paymentPage.navigateTo(baseUrl);

            // First payment - approved
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "25.00",
                    "EUR",
                    "4242424242424242",
                    "E2E Multi User",
                    "1225",
                    "123"
            );
            paymentPage.submitAndWaitForResult();
            assertTrue(paymentPage.isResultSuccess());
            String firstTransactionId = paymentPage.getTransactionId();

            // Refresh page for second payment
            paymentPage.navigateTo(baseUrl);

            // Second payment - declined
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "35.00",
                    "EUR",
                    "4000000000000002",
                    "E2E Multi User 2",
                    "1225",
                    "456"
            );
            paymentPage.submitAndWaitForResult();
            assertTrue(paymentPage.isResultError());
            String secondTransactionId = paymentPage.getTransactionId();

            // Transaction IDs should be different
            assertNotEquals(firstTransactionId, secondTransactionId);
        } catch (AssertionError | Exception e) {
            captureScreenshot("multiplePaymentAttempts");
            capturePageSource("multiplePaymentAttempts");
            throw e;
        }
    }

    @Test
    @Order(8)
    @DisplayName("E2E: Currency selection")
    void currencySelectionTest() {
        try {
            paymentPage.navigateTo(baseUrl);

            // Test with USD currency
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "50.00",
                    "USD",
                    "4242424242424242",
                    "E2E Currency User",
                    "1225",
                    "123"
            );
            paymentPage.submitAndWaitForResult();
            assertTrue(paymentPage.isResultSuccess());

            // Refresh and test with GBP
            paymentPage.navigateTo(baseUrl);
            paymentPage.fillPaymentForm(
                    "E2E_MERCHANT",
                    "45.00",
                    "GBP",
                    "4242424242424242",
                    "E2E Currency User GBP",
                    "1225",
                    "789"
            );
            paymentPage.submitAndWaitForResult();
            assertTrue(paymentPage.isResultSuccess());
        } catch (AssertionError | Exception e) {
            captureScreenshot("currencySelectionTest");
            capturePageSource("currencySelectionTest");
            throw e;
        }
    }
}
