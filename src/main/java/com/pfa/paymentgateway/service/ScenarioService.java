package com.pfa.paymentgateway.service;

import com.pfa.paymentgateway.dto.ScenarioRequest;
import com.pfa.paymentgateway.dto.ScenarioResponse;
import com.pfa.paymentgateway.model.Scenario;
import com.pfa.paymentgateway.repository.IScenarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing simulation scenarios.
 */
@Service
public class ScenarioService {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioService.class);

    private final IScenarioRepository scenarioRepository;

    public ScenarioService(IScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Create a new scenario.
     */
    public ScenarioResponse createScenario(ScenarioRequest request) {
        logger.info("Creating scenario: {}", request.getName());

        Scenario scenario = new Scenario();
        scenario.setName(request.getName());
        scenario.setMatchRules(request.getMatchRules());
        scenario.setAction(request.getAction());
        scenario.setDelayMs(request.getDelayMs());
        scenario.setResponseCode(request.getResponseCode());

        Scenario saved = scenarioRepository.save(scenario);
        return mapToResponse(saved);
    }

    /**
     * Get all scenarios.
     */
    public List<ScenarioResponse> getAllScenarios() {
        return scenarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get scenario by ID.
     */
    public Optional<ScenarioResponse> getScenarioById(String id) {
        return scenarioRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Get scenario by name.
     */
    public Optional<ScenarioResponse> getScenarioByName(String name) {
        return scenarioRepository.findByName(name).map(this::mapToResponse);
    }

    private ScenarioResponse mapToResponse(Scenario scenario) {
        ScenarioResponse response = new ScenarioResponse();
        response.setId(scenario.getId());
        response.setName(scenario.getName());
        response.setMatchRules(scenario.getMatchRules());
        response.setAction(scenario.getAction());
        response.setDelayMs(scenario.getDelayMs());
        response.setResponseCode(scenario.getResponseCode());
        return response;
    }
}
