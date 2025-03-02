# Translation Service

## Overview
Translation Service is a Spring Boot application designed to manage and serve translations across multiple locales and platforms.

## Prerequisites
- Docker
- Docker Compose

## Quick Start
1. Launch the Application
   ```bash
   docker-compose up -d
This command will:

* Build the Docker image
* Start the MySQL database
* Launch the Translation Service application


2. Accessing the Application

Swagger UI: http://localhost:8083/swagger-ui/index.html

### Credentials:

* Username: user
* Password: password

## Database Population
The application can automatically populate the database with a large number of test records.
Configuration options:

```
# Enable data population
app.data.populate=true
# Number of records to generate
app.data.count=100000
```

### Features of Data Population

* Generates translations across multiple locales
* Creates random tags and keys
* Supports scaling up to 100,000+ records
* Configurable record count

### Supported Locales

* English (en)
* French (fr)
* Spanish (es)
* German (de)
* Italian (it)

### Generated Tags

`mobile`, `web`, `desktop`, `admin`, `user`, `public`, `private`, `login`, `signup`, `dashboard`, `settings`, `error`

## API Documentation

Swagger UI provides interactive API documentation:

* URL: http://localhost:8083/swagger-ui/index.html
* Authentication: Token-based (Basic Auth with provided credentials)

### Key Endpoints

* Create Translation: POST `/api/translations`
* Update Translation: PUT `/api/translations/{id}`
* Get All Translations: GET `/api/translations`
* Search Translations: POST `/api/translations/search`
* Export Translations: GET `/api/translations/export`

## Performance Considerations

* Efficient database population mechanism
* Multi-threaded batch processing
* Configurable record generation
* Unique key generation

## Troubleshooting
### Database Connection Issues

* Ensure MySQL is running
* Check database credentials
* Verify network connectivity

### Data Population Problems

* Verify app.data.populate is set to true
* Check available system resources
* Monitor application logs

## Development
### Local Development Setup
Requires:

* Java 17+
* Maven
* MySQL

### Local Run
```bash
./mvnw spring-boot:run
```
### Test Coverage
The project maintains high test coverage to ensure reliability and stability of the codebase.
Running Tests with Coverage
```bash
./mvnw clean verify
```
### Coverage Report
After running tests, the JaCoCo coverage report can be found at:
```
target/site/jacoco/index.html
```
### Coverage Requirements
The project enforces the following coverage requirements:

Line coverage: 100%
Branch coverage: 100%
