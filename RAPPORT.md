# RAPPORT DE PROJET - Simulateur de Passerelle de Paiement

**Projet de Fin d'Année (PFA)**  
**Auteur:** Mohammed Yassine Aa  
**Date:** Décembre 2024

---

## Table des Matières

1. [Introduction](#1-introduction)
2. [Contexte et Objectifs](#2-contexte-et-objectifs)
3. [Architecture et Conception](#3-architecture-et-conception)
4. [Stack Technique](#4-stack-technique)
5. [Fonctionnalités Principales](#5-fonctionnalités-principales)
6. [API REST](#6-api-rest)
7. [Modèle de Données](#7-modèle-de-données)
8. [Stratégie de Tests](#8-stratégie-de-tests)
9. [Sécurité](#9-sécurité)
10. [CI/CD et DevOps](#10-cicd-et-devops)
11. [Structure du Projet](#11-structure-du-projet)
12. [Guide d'Utilisation](#12-guide-dutilisation)
13. [Conclusions et Perspectives](#13-conclusions-et-perspectives)

---

## 1. Introduction

Le **Simulateur de Passerelle de Paiement** est un projet académique développé dans le cadre d'un Projet de Fin d'Année (PFA). Il s'agit d'un environnement de test complet permettant aux développeurs de simuler et valider différents scénarios de paiement sans avoir recours à un véritable prestataire de services de paiement.

### 1.1 Problématique

Lors du développement d'applications e-commerce, les développeurs font face à plusieurs défis :
- **Coûts élevés** des environnements de test avec de vraies passerelles de paiement
- **Complexité** de simulation de scénarios d'erreur spécifiques
- **Délais** pour obtenir des accès aux environnements sandbox des fournisseurs
- **Risques** liés à l'utilisation de vraies données de carte en développement

### 1.2 Solution Proposée

Ce simulateur offre une solution complète permettant de :
- Tester tous les scénarios de paiement (succès, échec, fraude, timeout, etc.)
- Disposer d'une API REST compatible avec les standards de l'industrie
- Configurer des scénarios personnalisés via une interface admin
- Valider l'intégration via des tests automatisés (unitaires, d'intégration, E2E)

---

## 2. Contexte et Objectifs

### 2.1 Objectifs du Projet

#### Objectifs Principaux
1. **Fournir un environnement de test réaliste** pour les transactions de paiement
2. **Implémenter une API REST complète** suivant les meilleures pratiques
3. **Supporter plusieurs scénarios de paiement** configurables dynamiquement
4. **Garantir la qualité** par une couverture de tests exhaustive
5. **Automatiser le déploiement** via CI/CD

#### Objectifs Pédagogiques
- Maîtriser le framework **Spring Boot** et l'écosystème Java moderne
- Appliquer les principes **SOLID** et les design patterns
- Implémenter une architecture **REST** conforme
- Pratiquer le **TDD** (Test-Driven Development)
- Utiliser les outils de **CI/CD** (GitHub Actions)

### 2.2 Périmètre Fonctionnel

Le projet couvre :
- ✅ Traitement de transactions de paiement simulées
- ✅ Gestion de scénarios de test configurables
- ✅ Interface utilisateur de démonstration
- ✅ API REST complète avec validation
- ✅ Système de santé et monitoring
- ✅ Tests automatisés multi-niveaux
- ✅ Pipeline CI/CD complet

### 2.3 Limitations et Avertissements

⚠️ **Ce simulateur N'EST PAS conforme PCI-DSS** et est destiné **uniquement à des fins de test et de démonstration**. Il ne doit en aucun cas être utilisé en production avec de vraies données de paiement.

---

## 3. Architecture et Conception

### 3.1 Architecture Globale

Le projet suit une architecture en couches (Layered Architecture) classique :

```
┌─────────────────────────────────────────────┐
│         Couche Présentation (UI/API)        │
│  - Controllers REST                          │
│  - Templates Thymeleaf                       │
│  - Validation des entrées                    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Couche Métier (Business Logic)      │
│  - PaymentService                            │
│  - ScenarioService                           │
│  - Logique de simulation                     │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Couche Persistance (Data)           │
│  - TransactionRepository                     │
│  - ScenarioRepository                        │
│  - Stockage en mémoire (In-Memory)          │
└─────────────────────────────────────────────┘
```

### 3.2 Patterns de Conception Utilisés

#### 3.2.1 MVC (Model-View-Controller)
- **Model** : Entités Transaction, Scenario, TransactionStatus
- **View** : Templates Thymeleaf (payment.html, result.html)
- **Controller** : PaymentController, ScenarioController, DemoController

#### 3.2.2 Repository Pattern
Abstraction de la couche de persistance permettant :
- Séparation entre logique métier et accès aux données
- Facilité de migration vers une vraie base de données
- Testabilité améliorée (mocking facile)

#### 3.2.3 DTO (Data Transfer Object)
Séparation entre modèle de domaine et représentation API :
- `PaymentRequest` / `PaymentResponse`
- `ScenarioRequest` / `ScenarioResponse`
- Validation des données à la frontière du système

#### 3.2.4 Strategy Pattern (implicite)
Les scénarios de paiement implémentent un pattern stratégie :
- Chaque scénario définit un comportement spécifique
- Sélection dynamique du comportement basée sur les règles de matching
- Extensibilité pour ajouter de nouveaux scénarios

### 3.3 Principes SOLID Appliqués

1. **Single Responsibility Principle (SRP)**
   - Chaque classe a une responsabilité unique et bien définie
   - Séparation claire entre controllers, services, et repositories

2. **Open/Closed Principle (OCP)**
   - L'ajout de nouveaux scénarios ne nécessite pas de modification du code existant
   - Extension via configuration JSON

3. **Dependency Inversion Principle (DIP)**
   - Dépendance sur des abstractions (interfaces repository)
   - Injection de dépendances via Spring

---

## 4. Stack Technique

### 4.1 Technologies Backend

| Technologie | Version | Rôle |
|------------|---------|------|
| **Java** | 17 | Langage de programmation principal |
| **Spring Boot** | 3.2.0 | Framework d'application |
| **Spring Web** | - | API REST |
| **Spring Validation** | - | Validation des données |
| **Spring Actuator** | - | Monitoring et métriques |
| **Thymeleaf** | - | Moteur de templates |
| **Maven** | 3.8+ | Gestion de dépendances et build |
| **Lombok** | - | Réduction du code boilerplate |

### 4.2 Technologies de Test

| Technologie | Version | Rôle |
|------------|---------|------|
| **JUnit 5** | - | Framework de tests unitaires |
| **Spring Test** | - | Tests d'intégration Spring |
| **MockMvc** | - | Tests de contrôleurs |
| **RestAssured** | 5.4.0 | Tests d'API REST |
| **Selenium** | 4.15.0 | Tests End-to-End |
| **WebDriverManager** | 5.6.2 | Gestion des drivers Selenium |
| **Cucumber** | 7.14.0 | Tests BDD (Behavior-Driven Development) |

### 4.3 Outils DevOps

- **Git** : Contrôle de version
- **GitHub Actions** : CI/CD
- **GitHub** : Hébergement du code source
- **Maven** : Automatisation du build

### 4.4 Justification des Choix Techniques

#### Pourquoi Spring Boot ?
- **Productivité** : Configuration minimale, démarrage rapide
- **Écosystème riche** : Large gamme de starters et intégrations
- **Production-ready** : Actuator pour monitoring, gestion des erreurs
- **Communauté** : Documentation extensive, support actif

#### Pourquoi des tests multi-niveaux ?
- **Qualité** : Détection précoce des bugs
- **Confiance** : Refactoring sans régression
- **Documentation** : Les tests servent de documentation vivante

---

## 5. Fonctionnalités Principales

### 5.1 Traitement des Paiements

Le système simule le traitement complet d'une transaction de paiement :

1. **Réception de la requête** avec validation
2. **Extraction des informations** de la carte (masquage des données sensibles)
3. **Matching avec un scénario** configuré
4. **Application du comportement** du scénario (délai, réponse)
5. **Persistance de la transaction**
6. **Retour de la réponse** au client

#### Statuts de Transaction Supportés

| Statut | Description | Code Carte Test |
|--------|-------------|-----------------|
| `APPROVED` | Paiement approuvé | 4242424242424242 |
| `DECLINED` | Paiement refusé | 4000000000000002 |
| `FRAUD_SUSPECTED` | Fraude suspectée | 4000000000000069 |
| `TIMEOUT` | Timeout de traitement | 4000000000000077 |
| `THREE_DS_REQUIRED` | 3D Secure requis | 4000000000003220 |
| `ERROR` | Erreur interne | 4000000000000085 |

### 5.2 Gestion des Scénarios

Les scénarios permettent de configurer dynamiquement le comportement du simulateur :

#### Structure d'un Scénario
```json
{
  "name": "Approved Payment",
  "matchRules": {
    "cardNumber": "4242424242424242"
  },
  "action": "APPROVED",
  "delayMs": 0,
  "responseCode": "00"
}
```

#### Règles de Matching
Les scénarios peuvent matcher sur :
- **Numéro de carte** (cardNumber)
- **ID marchand** (merchantId)
- Combinaisons multiples

### 5.3 Interface de Démonstration

Interface web interactive permettant de :
- Saisir les informations de paiement via un formulaire
- Visualiser immédiatement le résultat de la transaction
- Tester différents scénarios en changeant le numéro de carte

**Technologie** : Thymeleaf avec Bootstrap pour le style

### 5.4 API de Santé

Endpoint `/api/health` fournissant :
- Statut du service (UP/DOWN)
- Statistiques des transactions
- Informations de configuration

---

## 6. API REST

### 6.1 Endpoints Disponibles

#### 6.1.1 Paiements

**POST /api/payments**
- **Description** : Soumettre une transaction de paiement
- **Corps de requête** : PaymentRequest (JSON)
- **Réponse** : PaymentResponse (JSON)
- **Codes HTTP** : 200 (OK), 400 (Bad Request), 500 (Error)

**Exemple de requête** :
```json
{
  "merchantId": "DEMO_MERCHANT",
  "amount": 100.00,
  "currency": "EUR",
  "cardNumber": "4242424242424242",
  "expiryDate": "12/25",
  "cvv": "123",
  "cardholderName": "John Doe"
}
```

**Exemple de réponse** :
```json
{
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "APPROVED",
  "message": "Payment approved",
  "amount": 100.00,
  "currency": "EUR",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**GET /api/payments/{transactionId}**
- **Description** : Récupérer l'état d'une transaction
- **Paramètre** : transactionId (UUID)
- **Réponse** : Transaction complète (JSON)
- **Codes HTTP** : 200 (OK), 404 (Not Found)

#### 6.1.2 Scénarios

**GET /api/scenarios**
- **Description** : Lister tous les scénarios configurés
- **Réponse** : Liste de ScenarioResponse (JSON)
- **Codes HTTP** : 200 (OK)

**POST /api/scenarios**
- **Description** : Créer un nouveau scénario (Admin)
- **Headers requis** : X-API-Key
- **Corps de requête** : ScenarioRequest (JSON)
- **Réponse** : ScenarioResponse (JSON)
- **Codes HTTP** : 201 (Created), 401 (Unauthorized), 400 (Bad Request)

#### 6.1.3 Santé

**GET /api/health**
- **Description** : État de santé du service
- **Réponse** : Informations de santé (JSON)
- **Codes HTTP** : 200 (OK)

#### 6.1.4 Interface Web

**GET /demo/payment**
- **Description** : Formulaire de paiement interactif
- **Réponse** : Page HTML

### 6.2 Validation des Données

Toutes les requêtes sont validées avec **Bean Validation** (JSR-380) :

| Champ | Validations |
|-------|-------------|
| merchantId | Non vide |
| amount | ≥ 0.01 |
| currency | 3 caractères (ISO 4217) |
| cardNumber | 13-19 chiffres |
| expiryDate | Format MM/YY |
| cvv | 3-4 chiffres |

### 6.3 Gestion des Erreurs

Le système implémente un gestionnaire d'exceptions global (`GlobalExceptionHandler`) qui :
- Capture toutes les exceptions
- Retourne des réponses JSON structurées
- Masque les détails techniques en production
- Log les erreurs pour le débogage

**Format de réponse d'erreur** :
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Card number must be 13-19 digits",
  "path": "/api/payments"
}
```

---

## 7. Modèle de Données

### 7.1 Entités Principales

#### 7.1.1 Transaction
Représente une transaction de paiement.

```java
public class Transaction {
    private String transactionId;      // UUID unique
    private String merchantId;         // ID du marchand
    private BigDecimal amount;         // Montant
    private String currency;           // Devise (EUR, USD, etc.)
    private TransactionStatus status;  // Statut de la transaction
    private String cardFingerprint;    // Hash SHA-256 de la carte
    private String declineCode;        // Code de refus (si applicable)
    private Instant createdAt;         // Date de création
    private Map<String, Object> metadata; // Métadonnées additionnelles
}
```

**Points clés** :
- Les numéros de carte **ne sont jamais stockés** en clair
- Utilisation d'empreintes (SHA-256) pour l'identification
- UUID pour garantir l'unicité des transactions

#### 7.1.2 Scenario
Définit un comportement de simulation.

```java
public class Scenario {
    private String id;                    // UUID unique
    private String name;                  // Nom descriptif
    private Map<String, String> matchRules; // Règles de matching
    private String action;                // Action (APPROVED, DECLINED, etc.)
    private Long delayMs;                 // Délai de simulation (ms)
    private String responseCode;          // Code de réponse
}
```

#### 7.1.3 TransactionStatus (Enum)
```java
public enum TransactionStatus {
    PENDING,
    APPROVED,
    DECLINED,
    ERROR,
    TIMEOUT,
    FRAUD_SUSPECTED,
    THREE_DS_REQUIRED
}
```

### 7.2 Persistance

**Choix d'implémentation** : Stockage en mémoire (In-Memory)

**Justification** :
- ✅ Simplicité pour un projet de démonstration
- ✅ Pas de dépendance externe (base de données)
- ✅ Performance maximale pour les tests
- ✅ Réinitialisation facile entre les tests

**Migration future** : L'architecture Repository Pattern facilite une migration vers :
- Spring Data JPA + PostgreSQL/MySQL
- MongoDB pour un stockage NoSQL
- Redis pour du caching haute performance

---

## 8. Stratégie de Tests

La qualité est assurée par une pyramide de tests complète.

### 8.1 Pyramide de Tests

```
          ╱╲
         ╱  ╲         E2E Tests (Selenium + BDD)
        ╱────╲        - Tests d'interface utilisateur
       ╱      ╲       - Scénarios Cucumber Gherkin
      ╱────────╲      
     ╱          ╲     API Tests (RestAssured)
    ╱────────────╲    - Tests des endpoints REST
   ╱              ╲   - Validation des contrats API
  ╱────────────────╲  
 ╱                  ╲ Integration Tests (MockMvc)
╱────────────────────╲ - Tests des contrôleurs
                        - Tests avec contexte Spring
        
────────────────────── Unit Tests (JUnit 5)
                       - Tests de logique métier
                       - Tests de validation
                       - Mocking des dépendances
```

### 8.2 Tests Unitaires

**Localisation** : `src/test/java/.../unit/`

**Couverture** :
- `PaymentServiceTest` : Logique de traitement des paiements
- `PaymentRequestValidationTest` : Validation des DTOs
- `ScenarioRepositoryTest` : Logique de matching des scénarios

**Outils** :
- JUnit 5
- Mockito pour le mocking
- AssertJ pour les assertions fluides

**Exemple** :
```java
@Test
void shouldApprovePaymentWhenMatchingScenario() {
    // Given
    PaymentRequest request = createValidRequest();
    Scenario approvalScenario = createApprovalScenario();
    
    // When
    PaymentResponse response = paymentService.processPayment(request);
    
    // Then
    assertThat(response.getStatus()).isEqualTo(APPROVED);
}
```

### 8.3 Tests d'Intégration

**Localisation** : `src/test/java/.../integration/`

**Couverture** :
- `PaymentControllerIntegrationTest`
- `ScenarioControllerIntegrationTest`
- `HealthControllerIntegrationTest`

**Outils** :
- Spring Test
- MockMvc
- @SpringBootTest

**Caractéristiques** :
- Contexte Spring complet chargé
- Simulation de requêtes HTTP
- Validation de la sérialisation JSON

### 8.4 Tests API (RestAssured)

**Localisation** : `src/test/java/.../api/`

**Couverture** :
- `PaymentApiTest` : Tests de l'API de paiement
- `ScenarioApiTest` : Tests de l'API des scénarios

**Outils** : RestAssured

**Avantages** :
- Syntaxe BDD (Given-When-Then)
- Validation JSON avancée
- Tests de contrat d'API

**Exemple** :
```java
given()
    .contentType(ContentType.JSON)
    .body(paymentRequest)
.when()
    .post("/api/payments")
.then()
    .statusCode(200)
    .body("status", equalTo("APPROVED"))
    .body("transactionId", notNullValue());
```

### 8.5 Tests End-to-End (Selenium)

**Localisation** : `src/test/java/.../e2e/`

**Couverture** :
- `PaymentE2ETest` : Tests du flux complet utilisateur
- Pattern Page Object (`PaymentPage`)

**Outils** :
- Selenium WebDriver
- WebDriverManager (gestion automatique des drivers)
- Chrome/Chromium

**Caractéristiques** :
- Tests du navigateur réel
- Validation de l'interface utilisateur
- Support mode headless pour CI
- Captures d'écran en cas d'échec

**Exemple de flux testé** :
1. Naviguer vers la page de paiement
2. Remplir le formulaire
3. Soumettre le paiement
4. Vérifier l'affichage du résultat

### 8.6 Tests BDD avec Cucumber

**Localisation** : `src/test/resources/features/` et `src/test/java/.../bdd/`

**Format** : Gherkin (langage naturel)

**Exemple de feature** :
```gherkin
Feature: Payment Processing
  As a customer
  I want to process payments
  So that I can complete my purchases

  Scenario: Successful payment with valid card
    Given I am on the payment page
    When I enter card number "4242424242424242"
    And I submit the payment
    Then I should see a success result
    And the status should be "APPROVED"
```

**Avantages** :
- Tests lisibles par les non-techniques
- Documentation vivante
- Collaboration Product Owner / Développeurs

### 8.7 Exécution des Tests

```bash
# Tests unitaires uniquement
mvn test -Dtest="**/unit/**"

# Tests d'intégration
mvn test -Dtest="**/integration/**"

# Tests API
mvn test -Dtest="**/api/**"

# Tests E2E (nécessite app en cours d'exécution)
mvn test -Pe2e -Dselenium.headless=true

# Tests BDD
mvn test -Pbdd -Dselenium.headless=true

# Tous les tests (sauf E2E)
mvn test
```

### 8.8 Rapports de Tests

Les rapports sont générés automatiquement :
- **Surefire Reports** : `target/surefire-reports/`
- **Cucumber Reports** : `target/cucumber-reports/`
- **Screenshots E2E** : `target/e2e-screenshots/` (en cas d'échec)

---

## 9. Sécurité

### 9.1 Protection des Données Sensibles

#### Numéros de Carte
- ❌ **Jamais stockés en clair**
- ✅ **Hachage SHA-256** pour créer une empreinte
- ✅ **Troncature** dans les logs (4 derniers chiffres uniquement)

**Implémentation** :
```java
private String createCardFingerprint(String cardNumber) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(cardNumber.getBytes());
    return HexFormat.of().formatHex(hash).substring(0, 16);
}
```

#### Données CVV
- ❌ **Jamais persistées** (même pas en hash)
- ✅ Utilisées uniquement pour la validation
- ✅ Effacées après traitement

### 9.2 Authentification API Admin

L'endpoint `/api/scenarios` (POST) nécessite une clé API :

**Header requis** : `X-API-Key: <clé>`

**Configuration** :
- Variable d'environnement : `API_ADMIN_KEY`
- Propriété application : `api.admin.key`
- Valeur par défaut (dev) : `admin-secret-key`

**Implémentation** : Intercepteur Spring vérifiant le header

### 9.3 Validation des Entrées

Toutes les entrées utilisateur sont validées :
- **Bean Validation** (annotations @NotBlank, @Pattern, etc.)
- **Validation métier** dans les services
- **Sanitization** des données

### 9.4 Gestion des Erreurs

- Messages d'erreur génériques pour l'utilisateur
- Détails techniques uniquement dans les logs
- Pas de stack traces exposées en production

### 9.5 Conformité PCI-DSS

⚠️ **AVERTISSEMENT** : Ce simulateur N'EST PAS conforme PCI-DSS.

**Raisons** :
- Stockage simplifié (in-memory)
- Pas de chiffrement au repos
- Pas d'audit trail complet
- Pas de tokenization

**Usage approprié** : Environnements de développement et test UNIQUEMENT.

---

## 10. CI/CD et DevOps

### 10.1 Pipeline CI (Intégration Continue)

**Fichier** : `.github/workflows/ci.yml`

**Déclencheurs** :
- Push sur branches `main` et `develop`
- Pull Requests vers `main` et `develop`

**Étapes** :
1. **Checkout** du code
2. **Setup JDK 17** avec cache Maven
3. **Build** : `mvn clean compile`
4. **Tests unitaires** : `mvn test -Dtest="**/unit/**"`
5. **Tests d'intégration** : `mvn test -Dtest="**/integration/**"`
6. **Tests API** : `mvn test -Dtest="**/api/**"`
7. **Upload des rapports** en artifacts

**Durée moyenne** : ~2-3 minutes

### 10.2 Pipeline E2E

**Fichier** : `.github/workflows/e2e.yml`

**Déclencheurs** :
- Déclenchement manuel (workflow_dispatch)
- Planification nocturne (optionnel)

**Étapes** :
1. Checkout du code
2. Setup JDK 17
3. **Démarrage de l'application** en arrière-plan
4. **Attente** que l'application soit prête (health check)
5. **Exécution tests E2E** en mode headless
6. **Capture d'écran** si échec
7. **Upload artifacts** (screenshots)

### 10.3 Badge de Statut

Le README affiche un badge indiquant le statut du build :

[![CI - Build and Test](https://github.com/mohammedyassinAa/Payment-Gateway-Simulator/actions/workflows/ci.yml/badge.svg)](https://github.com/mohammedyassinAa/Payment-Gateway-Simulator/actions/workflows/ci.yml)

### 10.4 Bonnes Pratiques DevOps Appliquées

1. **Automatisation complète** du build et tests
2. **Fast feedback** : résultats en quelques minutes
3. **Reproductibilité** : environnement conteneurisé (Actions)
4. **Artifacts** : sauvegarde des rapports et screenshots
5. **Branch protection** : tests obligatoires avant merge

---

## 11. Structure du Projet

```
Payment-Gateway-Simulator/
│
├── .github/
│   └── workflows/              # Workflows GitHub Actions
│       ├── ci.yml             # Pipeline CI (build + tests)
│       └── e2e.yml            # Pipeline E2E (Selenium)
│
├── automation/
│   └── selenium/
│       └── resources/         # Fixtures JSON pour tests
│           ├── scenarios.json
│           └── test-data.json
│
├── src/
│   ├── main/
│   │   ├── java/com/pfa/paymentgateway/
│   │   │   ├── config/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── controller/
│   │   │   │   ├── DemoController.java
│   │   │   │   ├── HealthController.java
│   │   │   │   ├── PaymentController.java
│   │   │   │   └── ScenarioController.java
│   │   │   ├── dto/
│   │   │   │   ├── PaymentRequest.java
│   │   │   │   ├── PaymentResponse.java
│   │   │   │   ├── ScenarioRequest.java
│   │   │   │   └── ScenarioResponse.java
│   │   │   ├── model/
│   │   │   │   ├── Scenario.java
│   │   │   │   ├── Transaction.java
│   │   │   │   └── TransactionStatus.java
│   │   │   ├── repository/
│   │   │   │   ├── ScenarioRepository.java
│   │   │   │   └── TransactionRepository.java
│   │   │   ├── service/
│   │   │   │   ├── PaymentService.java
│   │   │   │   └── ScenarioService.java
│   │   │   └── PaymentGatewaySimulatorApplication.java
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── payment.html    # Formulaire de paiement
│   │       │   └── result.html     # Page de résultat
│   │       └── application.properties
│   │
│   └── test/
│       ├── java/com/pfa/paymentgateway/
│       │   ├── api/              # Tests API (RestAssured)
│       │   │   ├── PaymentApiTest.java
│       │   │   └── ScenarioApiTest.java
│       │   ├── bdd/              # Tests BDD (Cucumber)
│       │   │   ├── stepdefinitions/
│       │   │   │   └── PaymentStepDefinitions.java
│       │   │   ├── CucumberSpringConfiguration.java
│       │   │   └── CucumberTestRunner.java
│       │   ├── e2e/              # Tests E2E (Selenium)
│       │   │   ├── pages/
│       │   │   │   └── PaymentPage.java
│       │   │   ├── BaseE2ETest.java
│       │   │   └── PaymentE2ETest.java
│       │   ├── integration/      # Tests d'intégration
│       │   │   ├── HealthControllerIntegrationTest.java
│       │   │   ├── PaymentControllerIntegrationTest.java
│       │   │   └── ScenarioControllerIntegrationTest.java
│       │   └── unit/             # Tests unitaires
│       │       ├── PaymentRequestValidationTest.java
│       │       ├── PaymentServiceTest.java
│       │       └── ScenarioRepositoryTest.java
│       │
│       └── resources/
│           └── features/
│               └── payment.feature  # Scénarios Gherkin
│
├── .gitignore
├── pom.xml                     # Configuration Maven
├── README.md                   # Documentation utilisateur
└── RAPPORT.md                  # Ce rapport (documentation technique)
```

### 11.1 Organisation par Package

**config/** : Configuration Spring (gestionnaire d'exceptions, etc.)  
**controller/** : Contrôleurs REST et vues  
**dto/** : Data Transfer Objects (requêtes/réponses)  
**model/** : Entités du domaine  
**repository/** : Couche d'accès aux données  
**service/** : Logique métier  

### 11.2 Séparation des Tests

Les tests sont organisés par type dans des packages dédiés :
- **unit/** : Tests unitaires isolés
- **integration/** : Tests avec contexte Spring
- **api/** : Tests de l'API REST
- **e2e/** : Tests d'interface utilisateur
- **bdd/** : Tests en langage naturel (Gherkin)

---

## 12. Guide d'Utilisation

### 12.1 Installation

#### Prérequis
- Java 17 ou supérieur
- Maven 3.8 ou supérieur
- Chrome/Chromium (pour les tests E2E uniquement)

#### Cloner le Repository
```bash
git clone https://github.com/mohammedyassinAa/Payment-Gateway-Simulator.git
cd Payment-Gateway-Simulator
```

### 12.2 Compilation

```bash
mvn clean compile
```

### 12.3 Démarrage de l'Application

```bash
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

### 12.4 Utilisation de l'Interface Web

1. Naviguer vers `http://localhost:8080/demo/payment`
2. Remplir le formulaire avec les informations de test
3. Utiliser l'une des cartes de test (voir section 12.6)
4. Cliquer sur "Soumettre le paiement"
5. Observer le résultat affiché

### 12.5 Utilisation de l'API

#### Soumettre un Paiement
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "DEMO_MERCHANT",
    "amount": 100.00,
    "currency": "EUR",
    "cardNumber": "4242424242424242",
    "expiryDate": "12/25",
    "cvv": "123",
    "cardholderName": "John Doe"
  }'
```

#### Récupérer une Transaction
```bash
curl http://localhost:8080/api/payments/{transactionId}
```

#### Lister les Scénarios
```bash
curl http://localhost:8080/api/scenarios
```

#### Créer un Scénario (Admin)
```bash
curl -X POST http://localhost:8080/api/scenarios \
  -H "Content-Type: application/json" \
  -H "X-API-Key: admin-secret-key" \
  -d '{
    "name": "Custom Scenario",
    "matchRules": {"cardNumber": "4111111111111111"},
    "action": "APPROVED",
    "delayMs": 1000,
    "responseCode": "00"
  }'
```

### 12.6 Cartes de Test

| Numéro de Carte | Comportement | Délai |
|-----------------|--------------|-------|
| 4242424242424242 | Approuvé | Aucun |
| 4000000000000002 | Refusé | Aucun |
| 4000000000000069 | Fraude suspectée | Aucun |
| 4000000000000077 | Timeout | 5 secondes |
| 4000000000003220 | 3D Secure requis | Aucun |
| 4000000000000085 | Erreur interne | Aucun |
| 4000000000009999 | Latence élevée | 3 secondes |

**Note** : Tous les autres champs peuvent contenir n'importe quelle valeur valide.

### 12.7 Exécution des Tests

Voir section 8.7 pour les commandes détaillées.

### 12.8 Variables d'Environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `server.port` | Port du serveur | 8080 |
| `API_ADMIN_KEY` | Clé API admin | admin-secret-key |
| `logging.level.com.pfa.paymentgateway` | Niveau de log | INFO |
| `selenium.headless` | Mode headless Selenium | false |
| `app.baseUrl` | URL de l'app (tests E2E) | http://localhost:8080 |

---

## 13. Conclusions et Perspectives

### 13.1 Objectifs Atteints

✅ **Simulateur fonctionnel** avec API REST complète  
✅ **Scénarios configurables** pour tous les cas d'usage  
✅ **Interface de démonstration** intuitive  
✅ **Couverture de tests exhaustive** (unitaires, intégration, API, E2E, BDD)  
✅ **Pipeline CI/CD** automatisé  
✅ **Architecture propre** suivant les bonnes pratiques  
✅ **Documentation complète** (README + Rapport)  

### 13.2 Compétences Acquises

**Techniques** :
- Maîtrise de **Spring Boot** et de son écosystème
- Conception d'**API REST** robustes
- **TDD** et pyramide de tests
- **Selenium** et tests E2E
- **Cucumber** et BDD
- **CI/CD** avec GitHub Actions

**Méthodologiques** :
- Architecture en couches
- Design patterns (Repository, Strategy, MVC)
- Principes SOLID
- Clean Code
- Documentation technique

### 13.3 Améliorations Futures

#### Court Terme
1. **Dashboard administrateur** pour gérer les scénarios via l'interface web
2. **Metrics détaillées** avec Micrometer et Prometheus
3. **Webhooks** pour notifier les événements de paiement
4. **Support multi-devises** avec taux de change

#### Moyen Terme
5. **Base de données persistante** (PostgreSQL)
6. **Cache Redis** pour les scénarios
7. **Conteneurisation Docker** avec docker-compose
8. **API Gateway** avec Spring Cloud Gateway
9. **Service de tokenization** pour les cartes

#### Long Terme
10. **Architecture microservices** (service paiement, service fraude, etc.)
11. **Event-driven architecture** avec Kafka
12. **Observabilité complète** (traces, logs, métriques)
13. **Infrastructure as Code** (Terraform/Kubernetes)

### 13.4 Limites Connues

- Stockage en mémoire (pas de persistance)
- Pas de vrai système d'authentification marchand
- Pas de gestion de sessions utilisateur
- Tests E2E dépendants de Chrome
- Pas de rate limiting

### 13.5 Leçons Apprises

1. **L'importance des tests** : La pyramide de tests a permis de détecter de nombreux bugs en amont
2. **CI/CD indispensable** : Automatisation = confiance pour refactorer
3. **Documentation vivante** : Les tests BDD servent de documentation
4. **Architecture évolutive** : Le pattern Repository facilite les évolutions futures
5. **Sécurité dès le départ** : Ne jamais stocker de données sensibles en clair

### 13.6 Conclusion Générale

Ce projet a permis de développer un **simulateur de passerelle de paiement complet et fonctionnel**, respectant les standards de l'industrie en termes d'architecture, de tests et de pratiques DevOps.

L'application atteint les objectifs fixés :
- ✅ Environnement de test réaliste
- ✅ API REST professionnelle
- ✅ Qualité garantie par les tests
- ✅ CI/CD automatisé

Au-delà de l'aspect technique, ce projet a été une opportunité d'appliquer concrètement les connaissances théoriques acquises durant la formation, en particulier :
- L'**architecture logicielle**
- Les **design patterns**
- Le **Test-Driven Development**
- Les **pratiques DevOps**

Le simulateur est prêt à être utilisé par des équipes de développement pour valider leurs intégrations de paiement, et son architecture modulaire permet de l'étendre facilement selon les besoins futurs.

---

## Annexes

### A. Glossaire

**API** : Application Programming Interface - Interface permettant la communication entre applications  
**BDD** : Behavior-Driven Development - Développement piloté par le comportement  
**CI/CD** : Continuous Integration / Continuous Deployment - Intégration et déploiement continus  
**DTO** : Data Transfer Object - Objet de transfert de données  
**E2E** : End-to-End - Tests de bout en bout  
**JWT** : JSON Web Token - Jeton d'authentification  
**MVC** : Model-View-Controller - Pattern architectural  
**PCI-DSS** : Payment Card Industry Data Security Standard - Norme de sécurité pour les paiements  
**REST** : Representational State Transfer - Style d'architecture pour les API  
**SHA-256** : Secure Hash Algorithm 256 bits - Algorithme de hachage  
**SOLID** : Principes de conception orientée objet  
**TDD** : Test-Driven Development - Développement piloté par les tests  
**UUID** : Universally Unique Identifier - Identifiant unique universel  

### B. Références

- **Spring Boot Documentation** : https://spring.io/projects/spring-boot
- **RestAssured Documentation** : https://rest-assured.io/
- **Selenium Documentation** : https://www.selenium.dev/documentation/
- **Cucumber Documentation** : https://cucumber.io/docs
- **PCI-DSS Standards** : https://www.pcisecuritystandards.org/
- **GitHub Actions** : https://docs.github.com/en/actions

### C. Remerciements

Ce projet a été réalisé dans le cadre du Projet de Fin d'Année (PFA).

Merci aux enseignants et encadrants pour leur accompagnement tout au long de ce projet.

---

**Fin du Rapport**

**Auteur** : Mohammed Yassine Aa  
**Date** : Décembre 2024  
**Version** : 1.0.0
