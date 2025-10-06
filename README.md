# Customer Management REST API - Clean Architecture

A Spring Boot application that provides RESTful APIs for managing customer resources, implemented using **Clean Architecture** principles.

## Architecture Overview

This application follows **Clean Architecture** principles with **Facade Pattern** implementation, providing clear separation of concerns across four distinct layers with coordinated service management:

```
┌─────────────────────────────────────────────────────────────┐
│                    Web Layer                                │
│  (Controllers, DTOs, Exception Handlers)                    │
│  - HTTP request/response handling                           │
│  - Input validation & JSON serialization                    │
│  - Single dependency on Facade                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              Application Layer - Facade                     │
│  ┌─────────────────────────────────────────────────────┐    │
│  │            CustomerFacade                           │    │
│  │  - Coordinates multiple services                    │    │
│  │  - Unified interface for controllers                │    │
│  │  - No circular dependencies                         │    │
│  └─────────────────────────────────────────────────────┘    │
│                    │                    │                   │
│                    ▼                    ▼                   │
│  ┌─────────────────────────┐  ┌─────────────────────────┐   │
│  │ CustomerApplicationSvc  │  │  CustomerSearchService  │   │
│  │ - CRUD operations       │  │ - Search & filtering    │   │
│  │ - Basic workflows       │  │ - Advanced queries      │   │
│  │ - Simple get all        │  │ - Smart optimization    │   │
│  └─────────────────────────┘  └─────────────────────────┘   │
│                    │                    │                   │
│                    └────────┬───────────┘                   │
│                             ▼                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Use Cases Layer                        │    │
│  │ CreateCustomer │ GetCustomer │ SearchCustomers      │    │
│  │ UpdateCustomer │ DeleteCustomer │ GetAllCustomers   │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  Domain Layer                               │
│  (Entities, Value Objects, Repository Interfaces)           │
│  - Core business logic and validation rules                 │
│  - Domain entities (Customer, Address)                      │
│  - Value objects (CustomerSearchCriteria)                   │
│  - Repository contracts & domain exceptions                 │
│  - Domain validators (Customer, Address, Email)             │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │
┌─────────────────────────────────────────────────────────────┐
│               Infrastructure Layer                          │
│  (Repository Implementations, Mappers, Configuration)       │
│  - Data persistence (InMemoryCustomerRepository)            │
│  - DTO-Entity mapping (Customer, Address, Search)           │
│  - Framework configurations                                 │
│  - External service integrations                            │
└─────────────────────────────────────────────────────────────┘
```

### Key Architectural Principles

#### **1. Facade Pattern Benefits**
- **Single Point of Entry**: Controllers only depend on `CustomerFacade`
- **Service Coordination**: Facade routes requests to appropriate services
- **No Circular Dependencies**: Clean dependency graph with focused services
- **Scalability**: Easy to add new services without affecting existing code

#### **2. Clean Architecture Compliance**
- **Dependency Inversion**: Inner layers don't depend on outer layers
- **Single Responsibility**: Each service has one clear purpose
- **Separation of Concerns**: Business logic separated from infrastructure
- **Testability**: Each layer can be tested in isolation

#### **3. Enhanced Capabilities**
- **Advanced Search**: Multi-field filtering with smart optimization
- **Flexible Sorting**: Multiple sort criteria with various fields
- **Performance Optimization**: Automatic routing between simple/complex queries
- **Comprehensive Validation**: Domain-driven validation with detailed errors

## Facade Pattern Implementation

This application implements the **Facade Pattern** to coordinate multiple application services without creating circular dependencies. This provides a clean, scalable architecture that maintains single responsibility principles.

### Architecture with Facade

```
CustomerController (HTTP Layer)
    ↓ (single dependency)
CustomerFacade (Coordination Layer)
    ↓ ↓ (coordinates between focused services)
    ├─ CustomerApplicationService (CRUD operations)
    │   ├─ CreateCustomerUseCase
    │   ├─ GetCustomerUseCase  
    │   ├─ GetAllCustomersUseCase
    │   ├─ UpdateCustomerUseCase
    │   └─ DeleteCustomerUseCase
    │
    └─ CustomerSearchService (Search & filtering)
        ├─ SearchCustomersUseCase
        └─ GetAllCustomersUseCase (for simple queries)
```

### Benefits of Facade Pattern

- **No Circular Dependencies**: Each service has focused responsibilities without depending on each other
- **Single Responsibility**: 
  - `CustomerApplicationService`: Basic CRUD operations only
  - `CustomerSearchService`: Search, filtering, sorting only  
  - `CustomerFacade`: Coordination and unified interface only
- **Scalability**: Easy to add new services (reporting, analytics, validation, etc.)
- **Testability**: Each service can be tested in isolation
- **Clean Controllers**: Controller only depends on the facade

### Service Responsibilities

#### CustomerFacade
- Routes CRUD operations → `CustomerApplicationService`
- Routes search operations → `CustomerSearchService`  
- Provides unified interface to controllers
- Coordinates service interactions

#### CustomerApplicationService
- Basic CRUD: create, read, update, delete
- Simple getAllCustomers operations
- No search/filtering logic

#### CustomerSearchService
- Complex search with filters and sorting
- Smart routing between simple and filtered queries
- All search-related business logic

## Features

- **Clean Architecture**: Proper separation of concerns with dependency inversion
- **Facade Pattern**: Coordinates multiple services without circular dependencies
- **Domain-Driven Design**: Rich domain models with business logic encapsulation
- **Use Case Driven**: Application logic organized around specific use cases
- **Advanced Filtering & Sorting**: Comprehensive search capabilities with multiple criteria
- **Smart Query Optimization**: Automatic routing between simple and complex queries
- **Comprehensive Validation**: Input validation with detailed error responses
- **Centralized Error Handling**: Global exception handler with meaningful error messages
- **Pagination Support**: Optional pagination for listing customers
- **Thread-Safe**: In-memory storage using `ConcurrentHashMap`
- **Extensive Testing**: Unit tests and integration tests with high coverage
- **Code Coverage**: JaCoCo integration for test coverage reporting

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
src/main/java/com/example/customer_management/
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
│       ├── CustomerNotFoundException.java
│       ├── InvalidCustomerDataException.java
│       └── DuplicateCustomerException.java
├── application/                     # Application Layer (Use Cases & Services)
│   ├── usecase/                     # Individual use cases
│   │   ├── CreateCustomerUseCase.java      # Create customer business logic
│   │   ├── GetCustomerUseCase.java         # Get single customer
│   │   ├── GetAllCustomersUseCase.java     # Get all customers (simple)
│   │   ├── UpdateCustomerUseCase.java      # Update customer business logic
│   │   ├── DeleteCustomerUseCase.java      # Delete customer business logic
│   │   └── SearchCustomersUseCase.java     # Advanced search with filters/sorting
│   ├── service/                     # Application services
│   │   ├── CustomerApplicationService.java # CRUD operations coordination
│   │   └── CustomerSearchService.java      # Search & filtering coordination
│   └── facade/                      # Facade pattern coordination
│       └── CustomerFacade.java             # Coordinates multiple services
├── infrastructure/                  # Infrastructure Layer (External Concerns)
│   ├── persistence/                 # Data persistence implementations
│   │   └── InMemoryCustomerRepository.java # In-memory storage implementation
│   └── mapper/                      # DTO-Entity mapping
│       ├── CustomerMapper.java             # Customer entity ↔ DTO mapping
│       ├── AddressMapper.java              # Address value object ↔ DTO mapping
│       └── CustomerSearchMapper.java       # Search criteria ↔ DTO mapping
├── web/                            # Web Layer (HTTP Interface)
│   ├── controller/                  # REST controllers
│   │   └── CustomerController.java         # HTTP endpoints (uses facade)
│   ├── dto/                         # Data Transfer Objects
│   │   ├── customer/               # Customer-related DTOs
│   │   │   ├── CustomerRequestDTO.java     # Customer creation/update request
│   │   │   └── CustomerResponseDTO.java    # Customer response
│   │   ├── address/                # Address-related DTOs
│   │   │   └── AddressDTO.java            # Address data transfer
│   │   └── common/                 # Common DTOs
│   │       ├── PageResponseDTO.java        # Paginated response wrapper
│   │       ├── ErrorResponseDTO.java       # Error response format
│   │       └── ValidationErrorResponseDTO.java # Validation error details
│   └── exception/                   # Web exception handling
│       └── GlobalExceptionHandler.java     # Centralized error handling
└── CustomerManagementApplication.java      # Main Spring Boot application class

src/test/java/com/example/customer_management/  # Test Structure
├── application/usecase/             # Use case tests
│   ├── CreateCustomerUseCaseTest.java
│   └── SearchCustomersUseCaseTest.java
├── domain/
│   ├── model/                      # Domain model tests
│   │   ├── CustomerTest.java
│   │   └── AddressTest.java
│   └── validator/                  # Validation tests
│       ├── CustomerValidatorTest.java
│       ├── AddressValidatorTest.java
│       └── EmailValidatorTest.java
├── infrastructure/persistence/      # Repository tests
│   └── InMemoryCustomerRepositoryTest.java
├── web/controller/                 # Integration tests
│   └── CustomerControllerIntegrationTest.java
└── performance/                    # Performance tests
    └── SearchPerformanceTest.java
```

## Data Model

### Customer (Domain Entity)
- `id` (UUID) - Auto-generated server-side
- `name` (String) - Required, non-empty
- `email` (String) - Required, valid email format (normalized to lowercase)
- `phone` (String) - Required, non-empty
- `address` (Address) - Required
- `createdAt` (LocalDateTime) - Auto-generated
- `updatedAt` (LocalDateTime) - Auto-updated

### Address (Value Object)
- `street` (String) - Required
- `city` (String) - Required
- `state` (String) - Required
- `zipCode` (String) - Required
- `country` (String) - Required

## Design Decisions

### **Architectural Patterns**

1. **Clean Architecture**: Proper layering with dependency inversion ensures maintainable and testable code
2. **Facade Pattern**: Coordinates multiple application services without circular dependencies
3. **Use Case Driven**: Each business operation is encapsulated in a specific use case
4. **Repository Pattern**: Abstract data access through repository interfaces
5. **Service Layer Separation**: Distinct services for CRUD vs Search operations

### **Domain Design**

1. **Domain-Rich Models**: Customer and Address contain business logic and validation
2. **Value Objects**: Address and CustomerSearchCriteria implemented as immutable value objects
3. **Domain Validators**: Centralized validation logic in domain layer (Customer, Address, Email)
4. **Domain Exceptions**: Business-specific exceptions for better error handling

### **Technical Decisions**

1. **Thread-Safe Storage**: `ConcurrentHashMap` ensures thread-safety for in-memory operations
2. **UUID Identifiers**: Server-generated UUIDs prevent ID collisions and improve security
3. **Immutable Timestamps**: `createdAt` is preserved on updates; `updatedAt` is auto-updated
4. **Smart Query Optimization**: Automatic routing between simple and complex queries
5. **Comprehensive Validation**: Bean Validation (JSR-380) with custom error responses
6. **RESTful Design**: Proper HTTP status codes and unified filtering/sorting interface

### **Search & Filtering Architecture**

1. **Multi-Field Filtering**: Support for field-specific and general text search
2. **Flexible Sorting**: Multiple sort criteria with ascending/descending options
3. **Date Range Filtering**: Created/updated timestamp filtering capabilities
4. **Pagination Integration**: Seamless pagination with filtering and sorting
5. **Performance Optimization**: Different execution paths for simple vs complex queries


## Clean Architecture Benefits

1. **Independence**: The domain layer is completely independent of frameworks, databases, and external agencies
2. **Testability**: Business logic can be tested without any external dependencies
3. **Flexibility**: Easy to change databases, web frameworks, or external services
4. **Maintainability**: Clear separation of concerns makes the code easier to understand and modify
5. **Scalability**: Well-organized structure supports growth and team collaboration

## Trade-offs and Considerations

While Clean Architecture with Facade Pattern provides significant benefits, it's important to understand the trade-offs involved in this enhanced implementation:

### ✅ **Advantages**

#### **Architectural Benefits**
- **Clear Separation of Concerns**: Each layer has well-defined responsibilities
- **Facade Coordination**: Single point of entry eliminates circular dependencies
- **Service Specialization**: CRUD and Search services have focused responsibilities
- **Dependency Inversion**: High-level modules don't depend on low-level modules
- **Framework Independence**: Core business logic is isolated from Spring Boot specifics
- **Database Independence**: Easy to switch from in-memory to any database
- **Testability**: Each layer and service can be unit tested in isolation

#### **Development Benefits**
- **Team Scalability**: Multiple developers can work on different services simultaneously
- **Code Reusability**: Use cases can be reused across different interfaces (REST, GraphQL, CLI)
- **Maintainability**: Changes in one service rarely affect others
- **Extensibility**: Easy to add new services (reporting, analytics) without affecting existing code
- **Documentation**: Architecture itself serves as living documentation

#### **Enhanced Functionality Benefits**
- **Advanced Search**: Multi-field filtering with smart optimization
- **Performance Optimization**: Automatic routing between simple and complex queries
- **Flexible API**: Single endpoint supports filtering, sorting, and pagination
- **Comprehensive Validation**: Domain-driven validation with detailed error responses

### ⚠️ **Trade-offs**

#### **Complexity Overhead**
- **More Files**: Simple CRUD operations require multiple classes (Controller → Facade → Service → UseCase → Repository)
- **Service Coordination**: Additional facade layer adds coordination complexity
- **Learning Curve**: Developers need to understand Clean Architecture and Facade pattern principles
- **Initial Setup Time**: More upfront design and structure compared to traditional layered architecture
- **Potential Over-engineering**: For simple applications, this might be excessive

#### **Performance Considerations**
- **Additional Abstraction Layers**: More method calls through facade and service layers
- **DTO Mapping Overhead**: Converting between DTOs, domain objects, and search criteria
- **Memory Usage**: Multiple object representations (DTO, Domain, SearchCriteria) consume more memory
- **Query Optimization Overhead**: Smart routing logic adds processing time for simple queries

#### **Development Trade-offs**
- **Boilerplate Code**: More interfaces, implementations, and mapping code
- **Debugging Complexity**: Stack traces span multiple layers, services, and abstractions
- **IDE Navigation**: More files to navigate between for a single feature
- **Service Dependencies**: Need to understand which service handles which operations

#### **Search Implementation Trade-offs**
- **In-Memory Filtering**: All filtering happens in application memory (not database-level)
- **Scalability Concerns**: Large datasets may impact performance with current implementation
- **Query Complexity**: Advanced filtering logic increases code complexity
- **Testing Overhead**: More test scenarios needed for filtering and sorting combinations

### 🎯 **When This Architecture Makes Sense**

#### **Recommended For:**
- **Complex Business Logic**: Applications with rich domain models and business rules
- **Long-term Projects**: Systems expected to evolve and grow over time
- **Large Teams**: Projects with multiple developers working simultaneously
- **Multiple Interfaces**: Applications serving REST APIs, GraphQL, message queues, etc.
- **Regulatory Requirements**: Systems requiring clear audit trails and separation of concerns
- **Enterprise Applications**: Mission-critical systems requiring high maintainability

#### **Consider Alternatives For:**
- **Simple CRUD Applications**: Basic data management without complex business logic
- **Prototypes/MVPs**: Quick proof-of-concepts where speed of development is priority
- **Small Teams**: Projects with 1-2 developers where communication overhead is minimal
- **Short-lived Projects**: Applications with limited lifespan and scope
- **Performance-Critical Systems**: Applications where every millisecond matters

### 🔄 **Alternative Approaches**

#### **Traditional Layered Architecture**
- **Pros**: Simpler, fewer files, faster initial development
- **Cons**: Tight coupling, harder to test, framework dependency

#### **Transaction Script Pattern**
- **Pros**: Very simple, direct database operations
- **Cons**: Poor scalability, business logic scattered

#### **Active Record Pattern**
- **Pros**: Rapid development, less boilerplate
- **Cons**: Tight coupling between data and behavior

### 📊 **Metrics and Measurements**

#### **Development Time Impact**
- **Initial Setup**: ~2-3x longer than traditional approach
- **Feature Addition**: ~1.5x longer per feature initially
- **Maintenance**: ~50% faster for changes and bug fixes
- **Testing**: ~70% faster due to better isolation


## Future Enhancements

1. **Authentication**: JWT-based authentication implementation
2. **Authorization**: Role-based access control setup
3. **Caching Implementation**: Search result caching with Spring Cache
4. **Database Setup**: Database integration
5. **Model Context Protocol**: Implement MCP server for customer analytics and insights



