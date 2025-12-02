package com.pfa.paymentgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Payment Gateway Simulator.
 * This is a demonstration environment for testing payment scenarios.
 * NOT PCI compliant - for testing purposes only.
 */
@SpringBootApplication
public class PaymentGatewaySimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentGatewaySimulatorApplication.class, args);
    }
}
