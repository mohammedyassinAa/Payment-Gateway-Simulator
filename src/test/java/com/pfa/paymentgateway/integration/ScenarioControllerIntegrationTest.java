package com.pfa.paymentgateway.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.paymentgateway.dto.ScenarioRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ScenarioController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ScenarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/scenarios - should return list of scenarios")
    void getAllScenarios_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/scenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))))
                .andExpect(jsonPath("$[*].name", hasItem("declined_card")));
    }

    @Test
    @DisplayName("POST /api/scenarios - with valid API key should create scenario")
    void createScenario_withApiKey_shouldCreate() throws Exception {
        ScenarioRequest request = new ScenarioRequest();
        request.setName("test_scenario_" + System.currentTimeMillis());
        request.setAction("DECLINED");
        request.setMatchRules(Map.of("cardNumberPrefix", "9999"));
        request.setResponseCode("test_decline");

        mockMvc.perform(post("/api/scenarios")
                        .header("X-API-Key", "admin-secret-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.action").value("DECLINED"));
    }

    @Test
    @DisplayName("POST /api/scenarios - without API key should return 401")
    void createScenario_withoutApiKey_shouldReturnUnauthorized() throws Exception {
        ScenarioRequest request = new ScenarioRequest();
        request.setName("unauthorized_scenario");
        request.setAction("DECLINED");

        mockMvc.perform(post("/api/scenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/scenarios - with invalid API key should return 401")
    void createScenario_withInvalidApiKey_shouldReturnUnauthorized() throws Exception {
        ScenarioRequest request = new ScenarioRequest();
        request.setName("unauthorized_scenario");
        request.setAction("DECLINED");

        mockMvc.perform(post("/api/scenarios")
                        .header("X-API-Key", "wrong-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
