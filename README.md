# Customer Management REST API

A Spring Boot application that provides RESTful APIs for managing customer resources, implemented with **domain-driven design principles** and a **flat package structure** using the **Facade Pattern**.

## Architecture Overview

This application uses a **flat package structure** with **Facade Pattern** implementation, organizing components by functionality rather than traditional architectural layers:

```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP Interface                           │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              CustomerController                     │    │
│  │  - REST endpoints and HTTP handling                 │    │
│  │  - Input validation & JSON serialization            │    │
│  │  - Single dependency on CustomerFacade              │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  Facade Coordination                       │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              CustomerFacade                         │    │
│  │  - Thin delegation layer                            │    │
│  │  - Unified interface for controllers                │    │
│  │  - Direct pass-through to CustomerService           │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  Business Logic                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              CustomerService                        │    │
│  │  - All customer operations (CRUD + Search)          │    │
│  │  - Business logic and validation coordination       │    │
│  │  - Advanced search with filtering & sorting         │    │
│  │  - Smart query optimization and pagination          │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  Domain Components                          │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Domain Models                          │    │
│  │  - Customer (entity with business logic)            │    │
│  │  - Address (value object)                           │    │
│  │  - CustomerSearchCriteria (search parameters)       │    │
│  │  - BaseEntity (common entity properties)            │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Domain Validators                      │    │
│  │  - CustomerValidator (business rules)               │    │
│  │  - AddressValidator (address validation)            │    │
│  │  - EmailValidator (email format validation)         │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Domain Exceptions                      │    │
│  │  - CustomerNotFoundException                        │    │
│  │  - InvalidCustomerDataException                     │    │
│  │  - InvalidAddressException, etc.                    │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │
┌─────────────────────────────────────────────────────────────┐
│                Repository Components                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │         Repository & Data Access                    │    │
│  │  - CustomerRepository (interface)                   │    │
│  │  - InMemoryCustomerRepository (implementation)      │    │
│  │  - Thread-safe ConcurrentHashMap storage            │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Data Mappers                           │    │
│  │  - CustomerMapper (Entity ↔ DTO conversion)         │    │
│  │  - AddressMapper (Address ↔ DTO conversion)         │    │
│  │  - CustomerSearchMapper (Search criteria mapping)   │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  Supporting Components                      │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              DTOs & Responses                       │    │
│  │  - CustomerRequestDTO, CustomerResponseDTO          │    │
│  │  - CustomerPatchRequestDTO, AddressDTO              │    │
│  │  - PageResponseDTO, ErrorResponseDTO                │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Exception Handling                     │    │
│  │  - GlobalExceptionHandler                           │    │
│  │  - Centralized error response formatting            │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Key Architectural Principles

#### **1. Simplified Facade Pattern Implementation**
- **Single Point of Entry**: Controllers only depend on `CustomerFacade`
- **Thin Delegation Layer**: Facade provides pass-through interface to `CustomerService`
- **Unified Interface**: Single facade coordinates all customer operations
- **Future Extensibility**: Ready for service decomposition when complexity grows

#### **2. Clean Architecture Compliance**
- **Dependency Inversion**: Inner layers don't depend on outer layers
- **Domain-Centric Design**: Business logic encapsulated in domain entities
- **Infrastructure Independence**: Domain layer isolated from framework concerns
- **Testability**: Each layer can be tested in isolation

#### **3. Centralized Business Logic**
- **Single Service Approach**: All customer operations handled by `CustomerService`
- **Comprehensive Operations**: CRUD, search, filtering, and sorting in one place
- **Smart Query Optimization**: Automatic routing between simple/complex queries
- **Domain Validation**: Business rules enforced through domain validators

#### **4. Enhanced Search Capabilities**
- **Multi-Field Filtering**: Search across name, email, phone, and address fields
- **Flexible Sorting**: Multiple sort criteria with ascending/descending options
- **Date Range Filtering**: Filter by creation and update timestamps
- **Performance Optimization**: Different execution paths for simple vs complex queries
- **Pagination Integration**: Seamless pagination with filtering and sorting

## Facade Pattern Implementation

This application implements a **simplified Facade Pattern** that provides a clean interface between the web layer and business logic, with room for future expansion as complexity grows.

### Current Architecture with Facade

```
CustomerController (HTTP Layer)
    ↓ (single dependency)
CustomerFacade (Thin Delegation Layer)
    ↓ (direct pass-through)
CustomerService (Centralized Business Logic)
    ├─ CRUD Operations
    │   ├─ createCustomer()
    │   ├─ getCustomerById()
    │   ├─ updateCustomer()
    │   ├─ patchCustomer()
    │   └─ deleteCustomer()
    │
    └─ Search & Filtering Operations
        ├─ getAllCustomersWithFiltering()
        ├─ searchCustomers()
        ├─ executeSearch() (with pagination)
        ├─ executeSimpleQuery() (optimized path)
        └─ applySorting() (multi-field sorting)
```

### Current Implementation Benefits

- **Single Point of Entry**: Controllers only interact with `CustomerFacade`
- **Clean Separation**: Web layer isolated from business logic implementation
- **Future-Ready**: Facade can easily coordinate multiple services as complexity grows
- **Testability**: Business logic can be tested independently of web concerns
- **Maintainability**: Changes to service implementation don't affect controllers

### Facade Responsibilities

#### CustomerFacade (Current Implementation)
- **Thin Delegation**: Direct pass-through to `CustomerService` methods
- **Unified Interface**: Single point of contact for all customer operations
- **Future Coordination**: Ready to coordinate multiple services when needed
- **Clean Abstraction**: Shields controllers from service implementation details

#### CustomerService (Current Implementation)
- **Comprehensive Business Logic**: All customer-related operations in one place
- **CRUD Operations**: Create, read, update, delete with full validation
- **Advanced Search**: Multi-field filtering, sorting, and pagination
- **Smart Optimization**: Different execution paths for simple vs complex queries
- **Domain Integration**: Uses domain validators and mappers for data integrity

### Evolution Path

As the application grows, the facade can easily evolve to coordinate multiple specialized services:

```
Future Architecture (when complexity increases):
CustomerFacade
    ├─ CustomerCRUDService (basic operations)
    ├─ CustomerSearchService (search & filtering)
    ├─ CustomerValidationService (complex validation)
    ├─ CustomerAnalyticsService (reporting & metrics)
    └─ CustomerNotificationService (events & notifications)
```

## Exception Handling Architecture

This application implements a comprehensive exception handling strategy that follows Clean Architecture principles with centralized error management and meaningful error responses.

### Exception Handling Layers

```
┌─────────────────────────────────────────────────────────────┐
│                Web Layer (GlobalExceptionHandler)           │
│  - HTTP status code mapping                                 │
│  - Error response formatting                                │
│  - User-friendly error messages                             │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │
┌─────────────────────────────────────────────────────────────┐
│              Application Layer (Use Cases)                  │
│  - Business logic validation                                │
│  - Domain exception translation                             │
│  - Use case specific error handling                         │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │
┌─────────────────────────────────────────────────────────────┐
│                Domain Layer (Domain Exceptions)             │
│  - Business rule violations                                 │
│  - Domain-specific error conditions                         │
│  - Rich error context and details                           │
└─────────────────────────────────────────────────────────────┘
```

### Exception Types and Hierarchy

#### **Domain Exceptions**
- **`CustomerNotFoundException`**: Thrown when a customer with the specified ID doesn't exist
- **`InvalidCustomerDataException`**: Thrown when customer data violates business rules
- **`InvalidAddressException`**: Thrown when address data violates business rules
- **`InvalidEmailFormatException`**: Thrown when email format is invalid
- **`CustomerMappingException`**: Thrown when customer entity mapping fails
- **`AddressMappingException`**: Thrown when address entity mapping fails

#### **Validation Exceptions**
- **`MethodArgumentNotValidException`**: Bean validation failures (JSR-380)

#### **System Exceptions**
- **`IllegalArgumentException`**: Invalid method arguments
- **`Exception`**: Generic catch-all for unexpected errors

### Global Exception Handler

The `GlobalExceptionHandler` provides centralized exception handling with the following capabilities:

#### **Error Response Format**
```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/customers",
  "details": [
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email must be a valid email address"
    }
  ]
}
```

#### **Exception Mapping Strategy**

| Exception Type                    | HTTP Status               | Response Format          |
|-----------------------------------|---------------------------|--------------------------|
| `CustomerNotFoundException`       | 404 Not Found             | Standard error response  |
| `InvalidCustomerDataException`    | 400 Bad Request           | Standard error response  |
| `InvalidAddressException`         | 400 Bad Request           | Validation error details |
| `InvalidEmailFormatException`     | 400 Bad Request           | Validation error details |
| `CustomerMappingException`        | 400 Bad Request           | Standard error response  |
| `AddressMappingException`         | 400 Bad Request           | Standard error response  |
| `DomainException` (Generic)       | 400 Bad Request           | Standard error response  |
| `MethodArgumentNotValidException` | 400 Bad Request           | Validation error details |
| `IllegalArgumentException`        | 400 Bad Request           | Standard error response  |
| `Exception` (Generic)             | 500 Internal Server Error | Standard error response  |

#### **Key Features**

1. **Centralized Handling**: All exceptions are caught and handled in one place
2. **Consistent Response Format**: All errors follow the same JSON structure
3. **Detailed Error Information**: Includes field-level validation errors
4. **HTTP Status Code Mapping**: Proper HTTP status codes for different error types
5. **Logging Integration**: All exceptions are logged with appropriate levels
6. **User-Friendly Messages**: Error messages are clear and actionable
7. **Security Considerations**: Sensitive information is not exposed in error responses

#### **Validation Error Handling**

The application provides detailed validation error responses for:

- **Field Validation**: Individual field validation errors with rejected values
- **Cross-Field Validation**: Business rule violations across multiple fields
- **Format Validation**: Data format and type validation errors
- **Constraint Validation**: Custom constraint violations

#### **Error Response Examples**

**Validation Error Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for customer data",
  "path": "/api/customers",
  "details": [
    {
      "field": "name",
      "rejectedValue": "",
      "message": "Name is required and cannot be empty"
    },
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email must be a valid email address"
    },
    {
      "field": "address.zipCode",
      "rejectedValue": "123",
      "message": "Zip code must be at least 5 characters long"
    }
  ]
}
```

**Business Logic Error Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Customer name cannot be null or empty",
  "path": "/api/customers"
}
```

**Not Found Error Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Customer with ID '123e4567-e89b-12d3-a456-426614174000' not found",
  "path": "/api/customers/123e4567-e89b-12d3-a456-426614174000"
}
```

### Exception Handling Best Practices

#### **Domain Layer**
- Throw domain-specific exceptions for business rule violations
- Include rich context in exception messages
- Use meaningful exception names that describe the business condition

#### **Application Layer**
- Translate domain exceptions to appropriate HTTP responses
- Add additional context when needed
- Handle cross-cutting concerns (logging, monitoring)

#### **Web Layer**
- Provide consistent error response format
- Map exceptions to appropriate HTTP status codes
- Ensure sensitive information is not exposed


## Project Structure

```
src/main/java/com/example/customermanagement/
├── domain/                          # Domain Layer (Core Business Logic)
│   ├── model/                       # Domain entities and value objects
│   │   ├── BaseEntity.java         # Base entity with common fields
│   │   ├── Customer.java           # Customer domain entity
│   │   ├── Address.java            # Address value object
│   │   └── CustomerSearchCriteria.java # Search criteria value object
│   ├── repository/                  # Repository interfaces
│   │   └── CustomerRepository.java # Customer repository contract
│   ├── validator/                   # Domain validation logic
│   │   ├── CustomerValidator.java  # Customer validation rules
│   │   ├── AddressValidator.java   # Address validation rules
│   │   └── EmailValidator.java     # Email validation logic
│   └── exception/                   # Domain exceptions
│       ├── DomainException.java            # Base domain exception
│       ├── CustomerNotFoundException.java  # Customer not found
│       ├── InvalidCustomerDataException.java # Invalid customer data
│       ├── InvalidAddressException.java    # Invalid address data
│       ├── InvalidEmailFormatException.java # Invalid email format
│       ├── InvalidDateFormatException.java # Invalid date format
│       ├── CustomerMappingException.java   # Customer mapping errors
│       └── AddressMappingException.java    # Address mapping errors
├── service/                         # Application Layer (Services)
│   └── CustomerService.java               # Main customer service (simplified architecture)
├── facade/                          # Facade Pattern (Coordination Layer)
│   └── CustomerFacade.java                 # Coordinates service operations
├── repository/                      # Infrastructure Layer (Data Access)
│   └── InMemoryCustomerRepository.java    # In-memory storage implementation
├── mapper/                          # Infrastructure Layer (Mapping)
│   ├── CustomerMapper.java                # Customer entity ↔ DTO mapping
│   ├── AddressMapper.java                 # Address value object ↔ DTO mapping
│   └── CustomerSearchMapper.java          # Search criteria ↔ DTO mapping
├── controller/                      # Web Layer (HTTP Interface)
│   └── CustomerController.java            # REST endpoints (uses facade)
├── dto/                            # Data Transfer Objects
│   ├── customer/                   # Customer-related DTOs
│   │   ├── CustomerRequestDTO.java        # Customer creation/update request
│   │   ├── CustomerResponseDTO.java       # Customer response
│   │   ├── CustomerPatchRequestDTO.java   # Partial update request
│   │   └── CustomerSearchRequestDTO.java  # Search request parameters
│   ├── address/                    # Address-related DTOs
│   │   └── AddressDTO.java               # Address data transfer
│   └── common/                     # Common DTOs
│       ├── PageResponseDTO.java           # Paginated response wrapper
│       ├── ErrorResponseDTO.java          # Error response format
│       └── ValidationErrorResponseDTO.java # Validation error details
├── common/exception/                # Web Layer (Exception Handling)
│   └── GlobalExceptionHandler.java        # Centralized error handling
└── CustomerManagementApplication.java     # Main Spring Boot application class

### Key Architectural Components

#### **HTTP Interface** (`controller/`)
- **Controllers**: REST endpoint definitions and HTTP handling
- **Single Dependency**: Only depends on `CustomerFacade`
- **Input Validation**: Bean validation and request processing

#### **Coordination Layer** (`facade/`)
- **Facade**: Unified interface and thin delegation layer
- **Service Coordination**: Routes requests to appropriate services
- **Future Extensibility**: Ready for multiple service coordination

#### **Business Logic** (`service/`)
- **Services**: All customer operations and business logic
- **CRUD Operations**: Create, read, update, delete functionality
- **Advanced Search**: Filtering, sorting, and pagination
- **Domain Integration**: Uses domain models and validators

#### **Domain Components** (`domain/`)
- **Entities**: `Customer`, `BaseEntity` with business logic
- **Value Objects**: `Address`, `CustomerSearchCriteria` (immutable)
- **Validators**: Domain-specific validation logic
- **Exceptions**: Business rule violation exceptions
- **Repository Interfaces**: Data access contracts

#### **Repository Components** (`repository/`, `mapper/`)R
- **Repository Implementation**: In-memory data storage
- **Mappers**: DTO ↔ Domain object transformations
- **Data Access**: Thread-safe storage operations

#### **Supporting Components** (`dto/`, `common/exception/`)
- **DTOs**: Request/response data structures
- **Exception Handling**: Centralized error response management
- **Common Utilities**: Shared components and utilities


## Getting Started

### Prerequisites

- **Java 21** or higher
- **Gradle 8.x** (wrapper included)
- **Git** for version control

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd customermanagement
   ```

2. **Build the project:**
   ```bash
   ./gradlew build
   ```

3. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the API:**
   - Base URL: `http://localhost:8080`
   - API Base: `http://localhost:8080/api/v1/customers`

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html

# Run code quality checks
./gradlew check

# Run PMD analysis
./gradlew pmdMain pmdTest

# Run CPD (Copy-Paste Detection)
./gradlew runCpd
```

### Development Tools

- **JaCoCo Coverage**: Minimum 80% coverage required
- **PMD Analysis**: Static code analysis with custom rules
- **Copy-Paste Detection**: Identifies code duplication
- **Lombok**: Reduces boilerplate code
- **Spring Boot DevTools**: Hot reload during development

## API Documentation

### Base URL
```
http://localhost:8080/api/v1/customers
```

### Endpoints

#### 1. Create Customer
```http
POST /api/v1/customers
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-123-4567",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```

**Response (201 Created):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-123-4567",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "createdAt": "2024-01-15T10:30:00.000Z",
  "updatedAt": "2024-01-15T10:30:00.000Z"
}
```

#### 2. Get Customer by ID
```http
GET /api/v1/customers/{id}
```

**Response (200 OK):** Same as create response

#### 3. Update Customer (Full)
```http
PUT /api/v1/customers/{id}
Content-Type: application/json

{
  "name": "John Smith",
  "email": "john.smith@example.com",
  "phone": "+1-555-987-6543",
  "address": {
    "street": "456 Oak Ave",
    "city": "Los Angeles",
    "state": "CA",
    "zipCode": "90210",
    "country": "USA"
  }
}
```

#### 4. Update Customer (Partial)
```http
PATCH /api/v1/customers/{id}
Content-Type: application/json

{
  "email": "newemail@example.com",
  "address": {
    "city": "San Francisco",
    "state": "CA"
  }
}
```

#### 5. Delete Customer
```http
DELETE /api/v1/customers/{id}
```

**Response (204 No Content)**

#### 6. Get All Customers (with optional filtering)
```http
GET /api/v1/customers?page=0&size=10&search=john&city=New York&sort=name:asc,createdAt:desc
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `search` (optional): General text search across all fields
- `name` (optional): Filter by customer name
- `email` (optional): Filter by email address
- `phone` (optional): Filter by phone number
- `city` (optional): Filter by city
- `state` (optional): Filter by state
- `country` (optional): Filter by country
- `zipCode` (optional): Filter by zip code
- `createdAfter` (optional): Filter by creation date (ISO format)
- `createdBefore` (optional): Filter by creation date (ISO format)
- `updatedAfter` (optional): Filter by update date (ISO format)
- `updatedBefore` (optional): Filter by update date (ISO format)
- `sort` (optional): Sort criteria (format: `field:direction`, e.g., `name:asc,createdAt:desc`)

**Response:**
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "+1-555-123-4567",
      "address": {
        "street": "123 Main St",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA"
      },
      "createdAt": "2024-01-15T10:30:00.000Z",
      "updatedAt": "2024-01-15T10:30:00.000Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

#### 7. Advanced Search
```http
GET /api/v1/customers/search?search=john&city=New York&sort=name:asc&page=0&size=10
```

**Query Parameters:** Same as Get All Customers, but with required pagination parameters

### Sorting Options

Supported sort fields:
- `name`: Customer name
- `email`: Email address
- `phone`: Phone number
- `city`: Address city
- `state`: Address state
- `country`: Address country
- `zipCode`: Address zip code
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

Sort directions:
- `asc`: Ascending order
- `desc`: Descending order

Multiple sort criteria can be combined:
```
sort=name:asc,createdAt:desc,city:asc
```

### Error Responses

All error responses follow a consistent format:

```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/customers",
  "details": [
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email must be a valid email address"
    }
  ]
}
```

### HTTP Status Codes

- `200 OK`: Successful GET, PUT, PATCH operations
- `201 Created`: Successful POST operations
- `204 No Content`: Successful DELETE operations
- `400 Bad Request`: Validation errors, invalid data
- `404 Not Found`: Customer not found
- `500 Internal Server Error`: Unexpected server errors




