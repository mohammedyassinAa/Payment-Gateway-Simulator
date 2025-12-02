package com.pfa.paymentgateway.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Scenario request DTO for admin endpoint.
 */
public class ScenarioRequest {

    @NotBlank(message = "Scenario name is required")
    private String name;

    private Map<String, String> matchRules;

    @NotBlank(message = "Action is required")
    private String action;

    private Long delayMs;

    private String responseCode;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getMatchRules() {
        return matchRules;
    }

    public void setMatchRules(Map<String, String> matchRules) {
        this.matchRules = matchRules;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(Long delayMs) {
        this.delayMs = delayMs;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}
