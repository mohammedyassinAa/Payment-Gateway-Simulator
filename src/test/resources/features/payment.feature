# language: en
@payment
Feature: Payment Processing
  As a customer
  I want to process payments through the payment gateway
  So that I can complete my purchases

  Background:
    Given I am on the payment page

  @approved
  Scenario: Successful payment with valid card
    When I enter merchant ID "E2E_MERCHANT"
    And I enter amount "100.00"
    And I select currency "EUR"
    And I enter card number "4242424242424242"
    And I enter cardholder name "John Doe"
    And I enter expiry date "1225"
    And I enter CVV "123"
    And I submit the payment
    Then I should see a success result
    And the status should be "APPROVED"
    And I should see a transaction ID

  @declined
  Scenario: Declined payment with declined card
    When I enter merchant ID "E2E_MERCHANT"
    And I enter amount "50.00"
    And I select currency "EUR"
    And I enter card number "4000000000000002"
    And I enter cardholder name "Jane Doe"
    And I enter expiry date "1225"
    And I enter CVV "456"
    And I submit the payment
    Then I should see an error result
    And the status should be "DECLINED"

  @fraud
  Scenario: Fraud suspected payment
    When I enter merchant ID "E2E_MERCHANT"
    And I enter amount "200.00"
    And I select currency "EUR"
    And I enter card number "4000000000000069"
    And I enter cardholder name "Fraud Test"
    And I enter expiry date "1225"
    And I enter CVV "789"
    And I submit the payment
    Then I should see an error result
    And the status should be "FRAUD_SUSPECTED"
    And the message should contain "fraud"

  @3ds
  Scenario: 3D Secure required payment
    When I enter merchant ID "E2E_MERCHANT"
    And I enter amount "150.00"
    And I select currency "EUR"
    And I enter card number "4000000000003220"
    And I enter cardholder name "3DS Test"
    And I enter expiry date "1225"
    And I enter CVV "321"
    And I submit the payment
    Then I should see a pending result
    And the status should be "THREE_DS_REQUIRED"
    And the result title should contain "3D Secure"

  @error
  Scenario: Internal error payment
    When I enter merchant ID "E2E_MERCHANT"
    And I enter amount "99.00"
    And I select currency "EUR"
    And I enter card number "4000000000000085"
    And I enter cardholder name "Error Test"
    And I enter expiry date "1225"
    And I enter CVV "111"
    And I submit the payment
    Then I should see an error result
    And the status should be "ERROR"

  @currencies
  Scenario Outline: Payment with different currencies
    When I enter merchant ID "E2E_MERCHANT"
    And I enter amount "<amount>"
    And I select currency "<currency>"
    And I enter card number "4242424242424242"
    And I enter cardholder name "Currency Test"
    And I enter expiry date "1225"
    And I enter CVV "123"
    And I submit the payment
    Then I should see a success result
    And the status should be "APPROVED"

    Examples:
      | amount | currency |
      | 100.00 | EUR      |
      | 50.00  | USD      |
      | 75.00  | GBP      |
