# Microservices with Spring Cloud Config Server

A microservices architecture demonstrating centralized configuration management with Spring Cloud Config Server and HashiCorp Vault for secure credential storage.

## Why Config Server?

In modern microservices architectures, configuration management presents significant challenges:

1. **Configuration Sprawl**: Without centralization, each service maintains its own configuration, leading to duplication and inconsistency.

2. **Environment-Specific Settings**: Services need different configurations for development, testing, and production environments.

3. **Sensitive Credentials**: Database passwords, API keys, and other secrets shouldn't be committed to code repositories.

4. **Dynamic Reconfiguration**: Services sometimes need configuration updates without redeployment.

Spring Cloud Config Server solves these problems by providing a centralized, version-controlled, environment-aware configuration system.

## Project Architecture

This project demonstrates a microservices architecture with:

```
┌─────────────────┐     ┌───────────────┐     ┌─────────────┐
│  Config Server  │◄────┤  Git Repo     │     │   Vault     │
│                 │     │  (Non-sensitive)     │ (Sensitive) │
└────────┬────────┘     └───────────────┘     └──────┬──────┘
         │                                           │
         │ Fetches config                  Fetches   │
         │ from Git                        secrets   │
         │                                           │
         ▼                                           │
┌─────────────────┐                                  │
│ Microservices   │◄─────────────────────────────────┘
│ - profile-service
│ - notification-service
└─────────────────┘
```

### Our Scenario: Multi-Environment Database Configuration

We have two microservices:
- **Profile Service**: Manages user profiles
- **Notification Service**: Handles notifications

Each service needs different database configurations for each environment:

```
                    │ Development      │ Testing          │ Production
────────────────────┼──────────────────┼──────────────────┼──────────────────
Profile Service DB  │ profile_db (dev) │ profile_db (test)│ profile_db (prod)
                    │ User: profile_dev_user
                    │ Pass: profile_dev_password
                    │ 
Notification Svc DB │ notification_db  │ notification_db  │ notification_db
                    │ User: notification_dev_user
                    │ Pass: notification_dev_password
```

## How Configuration Works

### 1. Git Repository (Non-Sensitive Config)

The Git repository contains YAML files with placeholders for sensitive data:

```yaml
# profile-service-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/profile_db
    username: ${database.username}
    password: ${database.password}
    driver-class-name: org.postgresql.Driver
```

### 2. HashiCorp Vault (Sensitive Data)

Vault stores the sensitive credentials at paths specific to each service and environment:

```
# At path: kv/profile-service/dev
database.username = profile_dev_user
database.password = profile_dev_password
```

### 3. Config Server (Integration Layer)

Config Server is the bridge that:
- Retrieves YAML files from Git based on service name and environment
- Fetches sensitive values from Vault at the matching path
- Replaces ${...} placeholders with actual values
- Delivers the complete configuration to microservices

### 4. Microservice Bootstrap

Each microservice identifies itself to Config Server:

```yaml
# bootstrap.yml
spring:
  application:
    name: profile-service  # Identifies which service this is
  profiles:
    active: dev           # Identifies which environment to use
  cloud:
    config:
      uri: http://localhost:8888  # Config Server location
```

## Setting Up The Project

### 1. Infrastructure Setup

Start the required infrastructure:
```bash
docker-compose up -d
```

This launches:
- PostgreSQL database for profile and notification services
- HashiCorp Vault for secure credential storage

### 2. Vault Configuration

Store the credentials in Vault:
```bash
# For profile-service dev environment
docker exec -it $(docker ps -q -f name=vault) /bin/sh -c "export VAULT_TOKEN=myroot && export VAULT_ADDR=http://127.0.0.1:8200 && vault kv put kv/profile-service/dev database.username=profile_dev_user database.password=profile_dev_password"
```

### 3. Start Services

Launch the Config Server:
```bash
cd config-server && mvn spring-boot:run
```

Launch the Profile Service:
```bash
cd profile-service && mvn spring-boot:run
```

## Verification

You can verify the correct configuration loading:

1. Check Config Server's merged configuration:
```bash
curl -u configuser:configpassword http://localhost:8888/profile-service/dev
```

2. Check if Profile Service received the correct configuration:
```bash
curl http://localhost:8081/actuator/env
```

## Benefits

This approach provides:

1. **Separation of Concerns**: Non-sensitive config in Git, sensitive credentials in Vault
2. **Environment-Specific Configuration**: Easy switching between dev/test/prod
3. **Service-Specific Contexts**: Each service gets only its relevant configuration
4. **Security**: Credentials never appear in code or Git repositories
5. **Centralized Management**: One place to update configuration

## Environment-Specific Configuration

The architecture supports different environments (dev, test, prod) through:

1. Different git configuration files (e.g., profile-service-dev.yml, profile-service-prod.yml)
2. Environment-specific paths in Vault (e.g., kv/profile-service/dev, kv/profile-service/prod)
3. Setting the active profile in each service's bootstrap.yml

## Adding a New Service

To add a new microservice to this architecture:

1. Create service-specific config files in Git (e.g., new-service.yml, new-service-dev.yml)
2. Add service-specific secrets to Vault (e.g., kv/new-service/dev)
3. Configure the service's bootstrap.yml to identify itself to Config Server:
   ```yaml
   spring:
     application:
       name: new-service
     profiles:
       active: dev
     cloud:
       config:
         uri: http://localhost:8888
   ``` 
   ``` 