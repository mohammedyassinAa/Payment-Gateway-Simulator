package com.pfa.paymentgateway.repository;

import com.pfa.paymentgateway.model.Scenario;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for scenarios with default scenarios pre-loaded.
 */
@Repository
public class ScenarioRepository implements IScenarioRepository {

    private final Map<String, Scenario> scenarios = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loadDefaultScenarios();
    }

    private void loadDefaultScenarios() {
        // Default scenario: Declined for card starting with 4000000000000002
        Scenario declinedScenario = new Scenario();
        declinedScenario.setName("declined_card");
        declinedScenario.setMatchRules(Map.of("cardNumberPrefix", "4000000000000002"));
        declinedScenario.setAction("DECLINED");
        declinedScenario.setResponseCode("card_declined");
        scenarios.put(declinedScenario.getId(), declinedScenario);

        // Fraud scenario: Cards starting with 4000000000000069
        Scenario fraudScenario = new Scenario();
        fraudScenario.setName("fraud_card");
        fraudScenario.setMatchRules(Map.of("cardNumberPrefix", "4000000000000069"));
        fraudScenario.setAction("FRAUD_SUSPECTED");
        fraudScenario.setResponseCode("fraud_suspected");
        scenarios.put(fraudScenario.getId(), fraudScenario);

        // Timeout scenario: Cards starting with 4000000000000077
        Scenario timeoutScenario = new Scenario();
        timeoutScenario.setName("timeout_card");
        timeoutScenario.setMatchRules(Map.of("cardNumberPrefix", "4000000000000077"));
        timeoutScenario.setAction("TIMEOUT");
        timeoutScenario.setDelayMs(5000L);
        timeoutScenario.setResponseCode("timeout");
        scenarios.put(timeoutScenario.getId(), timeoutScenario);

        // 3DS Required scenario: Cards starting with 4000000000003220
        Scenario threeDsScenario = new Scenario();
        threeDsScenario.setName("3ds_required");
        threeDsScenario.setMatchRules(Map.of("cardNumberPrefix", "4000000000003220"));
        threeDsScenario.setAction("THREE_DS_REQUIRED");
        threeDsScenario.setResponseCode("3ds_required");
        scenarios.put(threeDsScenario.getId(), threeDsScenario);

        // Error scenario: Cards starting with 4000000000000085
        Scenario errorScenario = new Scenario();
        errorScenario.setName("error_card");
        errorScenario.setMatchRules(Map.of("cardNumberPrefix", "4000000000000085"));
        errorScenario.setAction("ERROR");
        errorScenario.setResponseCode("internal_error");
        scenarios.put(errorScenario.getId(), errorScenario);

        // High latency scenario: Cards starting with 4000000000009999
        Scenario highLatencyScenario = new Scenario();
        highLatencyScenario.setName("high_latency");
        highLatencyScenario.setMatchRules(Map.of("cardNumberPrefix", "4000000000009999"));
        highLatencyScenario.setAction("APPROVED");
        highLatencyScenario.setDelayMs(3000L);
        highLatencyScenario.setResponseCode("approved");
        scenarios.put(highLatencyScenario.getId(), highLatencyScenario);
    }

    @Override
    public Scenario save(Scenario scenario) {
        scenarios.put(scenario.getId(), scenario);
        return scenario;
    }

    @Override
    public Optional<Scenario> findById(String id) {
        return Optional.ofNullable(scenarios.get(id));
    }

    @Override
    public Optional<Scenario> findByName(String name) {
        return scenarios.values().stream()
                .filter(s -> s.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Scenario> findAll() {
        return new ArrayList<>(scenarios.values());
    }

    @Override
    public Optional<Scenario> findMatchingScenario(String cardNumber, String merchantId) {
        return scenarios.values().stream()
                .filter(scenario -> matchesRules(scenario, cardNumber, merchantId))
                .findFirst();
    }

    private boolean matchesRules(Scenario scenario, String cardNumber, String merchantId) {
        if (scenario.getMatchRules() == null) {
            return false;
        }

        boolean matches = true;

        String cardPrefix = scenario.getMatchRules().get("cardNumberPrefix");
        if (cardPrefix != null && cardNumber != null) {
            matches = cardNumber.startsWith(cardPrefix);
        }

        String merchantIdRule = scenario.getMatchRules().get("merchantId");
        if (merchantIdRule != null && merchantId != null) {
            matches = matches && merchantId.equals(merchantIdRule);
        }

        return matches;
    }

    @Override
    public void deleteAll() {
        scenarios.clear();
    }

    @Override
    public int count() {
        return scenarios.size();
    }
}
