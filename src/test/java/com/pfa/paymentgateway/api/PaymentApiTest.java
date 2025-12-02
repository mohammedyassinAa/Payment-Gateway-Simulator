package com.pfa.paymentgateway.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API tests using RestAssured for payment endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentApiTest {

    @LocalServerPort
    private int port;

    private static String createdTransactionId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    @Order(1)
    @DisplayName("API: Submit approved payment")
    void submitPayment_approved_shouldReturn200() {
        Map<String, Object> request = createPaymentRequest("4242424242424242", "100.00");

        createdTransactionId = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/payments")
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"))
                .body("message", equalTo("Payment approved"))
                .body("transactionId", notNullValue())
                .body("amount", equalTo(100.0f))
                .body("currency", equalTo("EUR"))
                .extract()
                .path("transactionId");
    }

    @Test
    @Order(2)
    @DisplayName("API: Retrieve created transaction")
    void getTransaction_afterCreation_shouldReturnDetails() {
        Assumptions.assumeTrue(createdTransactionId != null, "Transaction must be created first");

        given()
                .when()
                .get("/payments/{id}", createdTransactionId)
                .then()
                .statusCode(200)
                .body("transactionId", equalTo(createdTransactionId))
                .body("status", equalTo("APPROVED"))
                .body("merchantId", equalTo("DEMO_MERCHANT"));
    }

    @Test
    @Order(3)
    @DisplayName("API: Submit declined payment")
    void submitPayment_declined_shouldReturn402() {
        Map<String, Object> request = createPaymentRequest("4000000000000002", "50.00");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/payments")
                .then()
                .statusCode(402)
                .body("status", equalTo("DECLINED"))
                .body("declineCode", equalTo("card_declined"));
    }

    @Test
    @Order(4)
    @DisplayName("API: Submit fraud suspected payment")
    void submitPayment_fraud_shouldReturn402() {
        Map<String, Object> request = createPaymentRequest("4000000000000069", "200.00");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/payments")
                .then()
                .statusCode(402)
                .body("status", equalTo("FRAUD_SUSPECTED"))
                .body("message", containsString("fraud"));
    }

    @Test
    @Order(5)
    @DisplayName("API: Submit 3DS required payment")
    void submitPayment_3ds_shouldReturn200WithRedirect() {
        Map<String, Object> request = createPaymentRequest("4000000000003220", "150.00");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/payments")
                .then()
                .statusCode(200)
                .body("status", equalTo("THREE_DS_REQUIRED"))
                .body("threeDsUrl", containsString("/3ds/authenticate"));
    }

    @Test
    @Order(6)
    @DisplayName("API: Submit error payment")
    void submitPayment_error_shouldReturn500() {
        Map<String, Object> request = createPaymentRequest("4000000000000085", "75.00");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/payments")
                .then()
                .statusCode(500)
                .body("status", equalTo("ERROR"))
                .body("message", containsString("error"));
    }

    @Test
    @Order(7)
    @DisplayName("API: Submit invalid payment request")
    void submitPayment_invalidRequest_shouldReturn400() {
        Map<String, Object> request = new HashMap<>();
        request.put("merchantId", "TEST");
        // Missing required fields

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/payments")
                .then()
                .statusCode(400)
                .body("status", equalTo("error"))
                .body("errors", notNullValue());
    }

    @Test
    @Order(8)
    @DisplayName("API: Get non-existing transaction")
    void getTransaction_nonExisting_shouldReturn404() {
        given()
                .when()
                .get("/payments/{id}", "non-existing-id-12345")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    @DisplayName("API: Health check")
    void healthCheck_shouldReturnUp() {
        given()
                .when()
                .get("/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("service", equalTo("payment-gateway-simulator"));
    }

    private Map<String, Object> createPaymentRequest(String cardNumber, String amount) {
        Map<String, Object> request = new HashMap<>();
        request.put("merchantId", "DEMO_MERCHANT");
        request.put("amount", Double.parseDouble(amount));
        request.put("currency", "EUR");
        request.put("cardNumber", cardNumber);
        request.put("expiryDate", "12/25");
        request.put("cvv", "123");
        request.put("cardholderName", "API Test User");
        return request;
    }
}
