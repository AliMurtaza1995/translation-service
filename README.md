# Translation Service
Overview
Translation Service is a Spring Boot application designed to manage and serve translations across multiple locales and platforms.

**Prerequisites**

Docker
Docker Compose

**Quick Start**

1. Launch the Application
   bashCopydocker-compose up -d
   This command will:

Build the Docker image
Start the PostgreSQL database
Launch the Translation Service application

2. Accessing the Application

Swagger UI: http://localhost:8083/swagger-ui/index.html
Credentials:

Username: user
Password: password



**Database Population**

Automatic Data Generation
The application can automatically populate the database with a large number of test records:

Open application.properties or set environment variables:

**Enable data population**

app.data.populate=true

**Number of records to generate**

app.data.count=100000

**Features of Data Population:**

Generates translations across multiple locales
Creates random tags and keys
Supports scaling up to 100,000+ records
Configurable record count

**Supported Locales**

English (en)
French (fr)
Spanish (es)
German (de)
Italian (it)

**Generated Tags**

mobile
web
desktop
admin
user
public
private
login
signup
dashboard
settings
error

**API Documentation**
Swagger UI provides interactive API documentation:

URL: http://localhost:8083/swagger-ui/index.html
Authentication: Token-based (Basic Auth with provided credentials)

**Key Endpoints**

Create Translation: POST /api/translations
Update Translation: PUT /api/translations/{id}
Get All Translations: GET /api/translations
Search Translations: POST /api/translations/search
Export Translations: GET /api/translations/export

**Performance Considerations**

Efficient database population mechanism
Multi-threaded batch processing
Configurable record generation
Unique key generation

**Configuration**
Environment Variables
You can configure the application using environment variables:

APP_DATA_POPULATE: Enable/disable data population (true/false)
APP_DATA_COUNT: Number of records to generate
SPRING_DATASOURCE_URL: Database connection URL
SPRING_DATASOURCE_USERNAME: Database username
SPRING_DATASOURCE_PASSWORD: Database password

**Troubleshooting**

Database Connection Issues

Ensure MySQL is running
Check database credentials
Verify network connectivity


**Data Population Problems**

Verify app.data.populate is set to true
Check available system resources
Monitor application logs



**Development**
Local Development Setup

**Requires:**

Java 17+
Maven
MySQL


**Local Run**

bashCopy./mvnw spring-boot:run
