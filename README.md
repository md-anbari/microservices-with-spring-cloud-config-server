# Microservices with Spring Cloud Config Server

A microservices architecture demonstrating Spring Cloud Config Server with Vault integration for secure credential management.

## Project Overview

This project demonstrates a microservices architecture that externalizes configuration using Spring Cloud Config Server. The primary goal is to separate application code from configuration, allowing for environment-specific settings without code changes.

Key features:
- External configuration management for microservices
- Secure credential storage using HashiCorp Vault
- Environment-specific configuration profiles
- Centralized configuration access point

## Architecture

The project consists of the following components:

- **Config Server**: Centralizes configuration from multiple sources:
  - Git repository for non-sensitive configuration
  - HashiCorp Vault for sensitive credentials
  - Combines both sources to provide a unified configuration API

- **Profile Service**: Manages user profile data
  - Stores and retrieves user profile information
  - Connects to PostgreSQL database

- **Notification Service**: Handles user notifications
  - Sends notifications through various channels
  - Communicates with Profile Service

## Configuration Management

### Configuration Sources

1. **Git Repository**: Stores non-sensitive configuration
   - Environment-specific property files
   - Common application settings
   - Service-specific configurations
   - Format: `{application-name}-{profile}.yml`

2. **HashiCorp Vault**: Stores sensitive credentials
   - Database credentials
   - API keys
   - Encryption keys
   - Other secrets

The Config Server combines these sources to present a unified configuration view to each service.

### Configuration Structure

Each service has two primary configuration files:

1. **bootstrap.yml**:
   - Loaded during the bootstrap phase
   - Contains configuration to connect to Config Server
   - Minimal settings needed before the main application context loads

2. **application.yml**:
   - Contains service-specific configurations
   - Can be overridden by Config Server
   - Used for local development defaults

### Benefits

- **Separation of concerns**: Application code is decoupled from configuration
- **Environment consistency**: Same codebase works across all environments
- **Security**: Sensitive data stored securely in Vault
- **Centralized management**: Single point for configuration changes
- **Runtime reconfiguration**: Services can reload configuration without restart
- **Version control**: Configuration history through Git

## Project Structure

The project uses a Maven multi-module structure:

```
microservices-with-spring-cloud-config-server/
├── config-server/                # Configuration server module
├── profile-service/              # Profile management service
├── notification-service/         # Notification handling service
├── docker-compose.yml           # Docker setup for infrastructure
└── pom.xml                      # Parent POM with common dependencies
```

Each module has its own Maven POM file that inherits from the parent, enabling consistent dependency management.

## Running the Project

1. Start the infrastructure components:
   ```
   docker-compose up -d
   ```

2. Start the Config Server:
   ```
   cd config-server
   mvn spring-boot:run
   ```

3. Start the Profile Service:
   ```
   cd profile-service
   mvn spring-boot:run
   ```

4. Start the Notification Service:
   ```
   cd notification-service
   mvn spring-boot:run
   ```

## Features

- **Centralized Configuration**: All application properties are stored in a centralized Git repository
- **Secure Credential Management**: Sensitive credentials are stored in Vault and combined with configuration
- **Microservices Communication**: Services communicate through REST APIs
- **Database Operations**: Profile service performs CRUD operations on PostgreSQL
- **Health Monitoring**: Spring Boot Actuator endpoints for health checks and monitoring

## Environment Setup

The project uses environment variables defined in `.env` for Docker Compose configuration. This provides:

- PostgreSQL database for service data storage
- Vault server for secret management
- Appropriate connection between services 