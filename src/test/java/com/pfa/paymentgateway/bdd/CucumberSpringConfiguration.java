package com.pfa.paymentgateway.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cucumber Spring configuration for BDD tests.
 * This class enables Spring context integration with Cucumber.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CucumberSpringConfiguration {
    // This class provides Spring context configuration for Cucumber tests
}
