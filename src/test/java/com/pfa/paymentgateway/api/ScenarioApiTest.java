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
 * API tests using RestAssured for scenario endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScenarioApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("API: List all scenarios")
    void listScenarios_shouldReturnList() {
        given()
                .when()
                .get("/scenarios")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(6)))
                .body("name", hasItem("declined_card"))
                .body("name", hasItem("fraud_card"))
                .body("name", hasItem("timeout_card"));
    }

    @Test
    @DisplayName("API: Create scenario with valid API key")
    void createScenario_withApiKey_shouldCreate() {
        String uniqueName = "api_test_scenario_" + System.currentTimeMillis();
        Map<String, Object> request = new HashMap<>();
        request.put("name", uniqueName);
        request.put("action", "DECLINED");
        request.put("matchRules", Map.of("cardNumberPrefix", "8888"));
        request.put("responseCode", "api_test_decline");
        request.put("delayMs", 100);

        given()
                .contentType(ContentType.JSON)
                .header("X-API-Key", "admin-secret-key")
                .body(request)
                .when()
                .post("/scenarios")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo(uniqueName))
                .body("action", equalTo("DECLINED"))
                .body("responseCode", equalTo("api_test_decline"));
    }

    @Test
    @DisplayName("API: Create scenario without API key should fail")
    void createScenario_withoutApiKey_shouldReturnUnauthorized() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "unauthorized_scenario");
        request.put("action", "DECLINED");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/scenarios")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("API: Create scenario with wrong API key should fail")
    void createScenario_withWrongApiKey_shouldReturnUnauthorized() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "unauthorized_scenario");
        request.put("action", "DECLINED");

        given()
                .contentType(ContentType.JSON)
                .header("X-API-Key", "wrong-api-key")
                .body(request)
                .when()
                .post("/scenarios")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("API: Create scenario with invalid data should return 400")
    void createScenario_invalidRequest_shouldReturnBadRequest() {
        Map<String, Object> request = new HashMap<>();
        // Missing required 'name' and 'action'

        given()
                .contentType(ContentType.JSON)
                .header("X-API-Key", "admin-secret-key")
                .body(request)
                .when()
                .post("/scenarios")
                .then()
                .statusCode(400);
    }
}
