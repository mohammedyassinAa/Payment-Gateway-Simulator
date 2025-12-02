package com.pfa.paymentgateway.controller;

import com.pfa.paymentgateway.dto.ScenarioRequest;
import com.pfa.paymentgateway.dto.ScenarioResponse;
import com.pfa.paymentgateway.service.ScenarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for scenario management.
 */
@RestController
@RequestMapping("/api/scenarios")
public class ScenarioController {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioController.class);

    private final ScenarioService scenarioService;

    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    /**
     * List all configured scenarios.
     * 
     * @return List of scenarios
     */
    @GetMapping
    public ResponseEntity<List<ScenarioResponse>> getAllScenarios() {
        logger.info("Fetching all scenarios");
        return ResponseEntity.ok(scenarioService.getAllScenarios());
    }

    /**
     * Get scenario by ID.
     * 
     * @param id Scenario identifier
     * @return Scenario details or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScenarioResponse> getScenario(@PathVariable String id) {
        logger.info("Fetching scenario: {}", id);
        return scenarioService.getScenarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new scenario (admin endpoint).
     * 
     * @param request Scenario configuration
     * @return Created scenario
     */
    @PostMapping
    public ResponseEntity<ScenarioResponse> createScenario(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @Valid @RequestBody ScenarioRequest request) {
        
        // Basic API key validation (in production, use proper authentication)
        if (apiKey == null || !apiKey.equals("admin-secret-key")) {
            logger.warn("Unauthorized scenario creation attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Creating scenario: {}", request.getName());
        ScenarioResponse response = scenarioService.createScenario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
