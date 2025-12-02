package com.pfa.paymentgateway.unit;

import com.pfa.paymentgateway.model.Scenario;
import com.pfa.paymentgateway.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScenarioRepository.
 */
class ScenarioRepositoryTest {

    private ScenarioRepository scenarioRepository;

    @BeforeEach
    void setUp() {
        scenarioRepository = new ScenarioRepository();
        scenarioRepository.init(); // Load default scenarios
    }

    @Test
    @DisplayName("Default scenarios should be loaded on initialization")
    void init_shouldLoadDefaultScenarios() {
        assertTrue(scenarioRepository.count() > 0);
        assertNotNull(scenarioRepository.findByName("declined_card"));
        assertNotNull(scenarioRepository.findByName("fraud_card"));
        assertNotNull(scenarioRepository.findByName("timeout_card"));
    }

    @Test
    @DisplayName("Should find matching scenario by card prefix")
    void findMatchingScenario_byCardPrefix_shouldMatch() {
        Optional<Scenario> scenario = scenarioRepository.findMatchingScenario("4000000000000002", "MERCHANT");
        assertTrue(scenario.isPresent());
        assertEquals("declined_card", scenario.get().getName());
    }

    @Test
    @DisplayName("Should not match when card prefix doesn't match")
    void findMatchingScenario_noMatch_shouldReturnEmpty() {
        Optional<Scenario> scenario = scenarioRepository.findMatchingScenario("4242424242424242", "MERCHANT");
        assertFalse(scenario.isPresent());
    }

    @Test
    @DisplayName("Should save and retrieve custom scenario")
    void save_customScenario_shouldBeRetrievable() {
        Scenario customScenario = new Scenario();
        customScenario.setName("custom_test");
        customScenario.setAction("DECLINED");
        customScenario.setMatchRules(Map.of("cardNumberPrefix", "5555"));
        customScenario.setResponseCode("custom_decline");

        scenarioRepository.save(customScenario);

        Optional<Scenario> retrieved = scenarioRepository.findByName("custom_test");
        assertTrue(retrieved.isPresent());
        assertEquals("custom_decline", retrieved.get().getResponseCode());
    }

    @Test
    @DisplayName("Should find all scenarios")
    void findAll_shouldReturnAllScenarios() {
        var scenarios = scenarioRepository.findAll();
        assertFalse(scenarios.isEmpty());
        assertTrue(scenarios.size() >= 6); // At least the default scenarios
    }

    @Test
    @DisplayName("deleteAll should clear all scenarios")
    void deleteAll_shouldClearScenarios() {
        scenarioRepository.deleteAll();
        assertEquals(0, scenarioRepository.count());
    }
}
