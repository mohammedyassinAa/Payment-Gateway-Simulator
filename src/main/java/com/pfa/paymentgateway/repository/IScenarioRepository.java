package com.pfa.paymentgateway.repository;

import com.pfa.paymentgateway.model.Scenario;

import java.util.List;
import java.util.Optional;

/**
 * Interface for scenario repository operations.
 * Provides an abstraction layer for scenario persistence and matching.
 */
public interface IScenarioRepository {

    /**
     * Save a scenario.
     * @param scenario the scenario to save
     * @return the saved scenario
     */
    Scenario save(Scenario scenario);

    /**
     * Find a scenario by ID.
     * @param id the scenario ID
     * @return an Optional containing the scenario if found
     */
    Optional<Scenario> findById(String id);

    /**
     * Find a scenario by name.
     * @param name the scenario name
     * @return an Optional containing the scenario if found
     */
    Optional<Scenario> findByName(String name);

    /**
     * Find all scenarios.
     * @return a list of all scenarios
     */
    List<Scenario> findAll();

    /**
     * Find a scenario that matches the given card number and merchant ID.
     * @param cardNumber the card number to match
     * @param merchantId the merchant ID to match
     * @return an Optional containing the matching scenario if found
     */
    Optional<Scenario> findMatchingScenario(String cardNumber, String merchantId);

    /**
     * Delete all scenarios.
     */
    void deleteAll();

    /**
     * Count all scenarios.
     * @return the number of scenarios
     */
    int count();
}
