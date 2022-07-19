# Edge service

Spring-boot application with REST API for operations for work with another microservice that call CORE.

Using the postgresql database, keycloak auth2

## Getting Started

To rise this service execute the following commands:

    1. Run mvn clean package

    2. Run docker-compose up
    
    3. connect http://localhost:8080
## Deployment
Deployment variables:
You need to set env var over docker-compose file
EMAIL_USER
EMAIL_PASS

## Built With
* [Spring Boot](https://start.spring.io/) - An application framework and inversion of control container for the Java
  platform.
* [Maven](https://maven.apache.org/) - Dependency Management
  Footer