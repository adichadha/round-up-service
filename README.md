# Round-Up Service
The Round-Up Service is a Spring Boot application that interacts with Starling Bank's public APIs to perform various financial operations. The application is designed to calculate round-up amounts from transactions, create savings goals, and add round-up amounts to the specified savings goals.


## Table of Contents
* [Prerequisites](#Prerequisites)
* [Getting Started](#Getting Started)
* [Configuration](3Configuration)
* [API Endpoints](#API Endpoints)
* [Dependencies](#Dependencies)
* [Build and Run](#Build and Run)
* [Testing](#Testing)


## Prerequisites
Before running the Round-Up Service, ensure you have the following:

* Java 17
* Maven
* Starling Bank API credentials
* Internet connectivity


## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/round-up-service.git
```

2. Navigate to the project directory:
```bash
cd round-up-service
```

## Configuration
The application is configured using the application.yml file. Ensure you provide the necessary configurations, including Starling Bank API domain.

## API Endpoints
The Round-Up Service exposes the following API endpoints:

### * Perform Round-Up:
* Endpoint: `/round-up-service/api/v1/perform`
* Method: `POST`
* Headers:
    * `Authorization` : Bearer Token
* Request Body:
    * RoundUpRequestData

## Dependencies
The application relies on the following dependencies:

* Spring Boot Starter Web
* Lombok
* Spring Boot Starter Validation
* Jackson Datatype JSR310
* Spring Boot Starter Test


## Build and Run
Build and run the application using Maven:

```bash
mvn clean install
mvn spring-boot:run
```
The application will be accessible at `http://localhost:8081`.

## Testing
Written tests for the application components, including controllers, services, and clients. Execute tests using the following Maven command:


```bash
mvn test
```
