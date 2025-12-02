package com.pfa.paymentgateway.model;

import java.util.Map;
import java.util.UUID;

/**
 * Scenario entity representing a simulation scenario configuration.
 */
public class Scenario {
    
    private String id;
    private String name;
    private Map<String, String> matchRules;
    private String action;
    private Long delayMs;
    private String responseCode;

    public Scenario() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
