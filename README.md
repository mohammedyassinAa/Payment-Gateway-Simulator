# Payment Gateway Simulator

[![CI - Build and Test](https://github.com/mohammedyassinAa/Payment-Gateway-Simulator/actions/workflows/ci.yml/badge.svg)](https://github.com/mohammedyassinAa/Payment-Gateway-Simulator/actions/workflows/ci.yml)

Simulateur de passerelle de paiement pour projet PFA (Projet de Fin d'Année). Un environnement de test pour développer et valider des scénarios de paiement sans utiliser un vrai prestataire de paiement.

> ⚠️ **Avertissement**: Ce simulateur n'est PAS conforme PCI-DSS. Il est destiné uniquement à des fins de test et de démonstration.

## 🚀 Fonctionnalités

- **API REST** pour simuler des transactions de paiement
- **Comportements configurables**: APPROVED, DECLINED, FRAUD, TIMEOUT, ERROR, 3DS
- **Interface de démonstration** pour tester les paiements visuellement
- **Tests automatisés complets**: unitaires, intégration, API (RestAssured), E2E (Selenium)
- **CI/CD** avec GitHub Actions

## 📋 Prérequis

- Java 17+
- Maven 3.8+
- Chrome/Chromium (pour les tests E2E)

## 🛠️ Installation et Démarrage

### Cloner le repository

```bash
git clone https://github.com/mohammedyassinAa/Payment-Gateway-Simulator.git
cd Payment-Gateway-Simulator
```

### Compiler le projet

```bash
mvn clean compile
```

### Lancer l'application

```bash
mvn spring-boot:run
```

L'application sera disponible sur `http://localhost:8080`

## 📡 API Endpoints

### Paiements

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/payments` | Soumettre un paiement |
| GET | `/api/payments/{transactionId}` | Récupérer l'état d'une transaction |

### Scénarios

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/scenarios` | Lister les scénarios configurés |
| POST | `/api/scenarios` | Créer un scénario (admin, nécessite X-API-Key) |

### Santé

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/health` | État de santé du service |

### Interface de démonstration

| Endpoint | Description |
|----------|-------------|
| `/demo/payment` | Formulaire de paiement interactif |

## 💳 Cartes de Test

| Numéro de carte | Comportement |
|-----------------|--------------|
| `4242424242424242` | Approuvé |
| `4000000000000002` | Refusé |
| `4000000000000069` | Fraude suspectée |
| `4000000000000077` | Timeout (5s) |
| `4000000000003220` | 3DS requis |
| `4000000000000085` | Erreur interne |
| `4000000000009999` | Latence élevée (3s) |

## 🧪 Tests

### Exécuter tous les tests (sauf E2E)

```bash
mvn test
```

### Tests unitaires uniquement

```bash
mvn test -Dtest="**/unit/**"
```

### Tests d'intégration uniquement

```bash
mvn test -Dtest="**/integration/**"
```

### Tests API uniquement

```bash
mvn test -Dtest="**/api/**"
```

### Tests E2E Selenium

Les tests E2E nécessitent que l'application soit en cours d'exécution.

1. **Démarrer l'application** dans un terminal:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=test
   ```

2. **Exécuter les tests E2E** dans un autre terminal:
   ```bash
   mvn test -Pe2e -Dapp.baseUrl=http://localhost:8080
   ```

3. **Mode headless** (pour CI):
   ```bash
   mvn test -Pe2e -Dselenium.headless=true -Dapp.baseUrl=http://localhost:8080
   ```

### Rapports de test

Les rapports sont générés dans `target/surefire-reports/`.
Les captures d'écran E2E (en cas d'échec) sont dans `target/e2e-screenshots/`.

## 🔧 Configuration

### application.properties

```properties
# Port du serveur
server.port=8080

# Niveau de log
logging.level.com.pfa.paymentgateway=DEBUG
```

### Variables d'environnement pour les tests E2E

| Variable | Description | Défaut |
|----------|-------------|--------|
| `selenium.headless` | Mode sans affichage | `false` |
| `app.baseUrl` | URL de l'application | `http://localhost:8080` |

## 📁 Structure du Projet

```
Payment-Gateway-Simulator/
├── src/
│   ├── main/
│   │   ├── java/com/pfa/paymentgateway/
│   │   │   ├── controller/    # Contrôleurs REST
│   │   │   ├── service/       # Services métier
│   │   │   ├── repository/    # Repositories in-memory
│   │   │   ├── model/         # Entités
│   │   │   ├── dto/           # DTOs
│   │   │   └── config/        # Configuration
│   │   └── resources/
│   │       ├── templates/     # Templates Thymeleaf (UI)
│   │       └── application.properties
│   └── test/
│       └── java/com/pfa/paymentgateway/
│           ├── unit/          # Tests unitaires
│           ├── integration/   # Tests d'intégration
│           ├── api/           # Tests API RestAssured
│           └── e2e/           # Tests E2E Selenium
├── automation/selenium/       # Ressources Selenium
│   ├── pages/                 # Page Objects
│   ├── tests/                 # Classes de test
│   └── resources/             # Fixtures JSON
├── .github/workflows/         # GitHub Actions
└── pom.xml
```

## 🔒 Sécurité

- Les numéros de carte ne sont **jamais** stockés en clair
- Seules des empreintes (hash SHA-256) sont conservées
- L'endpoint admin `/api/scenarios` nécessite une clé API

### Clé API Admin

Header: `X-API-Key: admin-secret-key`

## 🚢 CI/CD

### Workflow CI (`.github/workflows/ci.yml`)
- Déclenché sur push/PR vers `main` et `develop`
- Compile le code
- Exécute les tests unitaires, d'intégration et API

### Workflow E2E (`.github/workflows/e2e.yml`)
- Déclenché manuellement ou chaque nuit
- Lance l'application
- Exécute les tests Selenium en mode headless
- Archive les captures d'écran en cas d'échec

## 📊 Couverture de Test

- **Tests unitaires**: Validation DTO, logique service, repository
- **Tests d'intégration**: Contrôleurs avec MockMvc
- **Tests API**: Tous les endpoints avec RestAssured
- **Tests E2E**: Flux utilisateur complets via Selenium

## 📝 Exemple de Requête API

### Soumettre un paiement

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

### Réponse (Approuvé)

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

## 📄 Licence

Ce projet est développé dans le cadre d'un projet académique (PFA).

## 👤 Auteur

Mohammed Yassine Aa