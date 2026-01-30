# Interview Scheduling System - Design Document

## 1. Problem Statement

    Interview scheduling is traditionally handled through manual coordination using emails or messaging platforms. This approach introduces multiple challenges such as scheduling conflicts, delayed responses, lack of real-time availability visibility, and poor candidate experience.

    The objective of this system is to design and implement a backend service that allows interviewers to expose interview availability and enables candidates to book interview slots in a safe, consistent, and scalable manner while preventing double booking under concurrent access.

### 1.1 Why Solve this Problem

    - Manual interview scheduling does not scale and often leads to delays, human errors, and scheduling conflicts.
    - Interview scheduling requires strong concurrency control to prevent multiple candidates from booking the same slot.
    - Correct handling of time zones and timestamps is essential to avoid inconsistent scheduling behavior.
    - Automating interview scheduling reduces recruiter effort and improves candidate experience through instant confirmations.
    - The problem represents a real-world backend challenge involving consistency, transactions, and resource contention.


## 2. Scope of the Project

    - The project focuses on enabling interviewers to create interview slots and candidates to book available slots through backend APIs.
    - It includes designing a relational database schema to persist slots, candidates, and booking information reliably.
    - Preventing double booking and handling concurrent slot booking requests is explicitly within the project scope.
    - The system standardizes time handling using timestamps to ensure consistent scheduling across time zones.
    - The application is designed to run in test and production environments using the same codebase with configuration-based setup.


## 3. Architecture

    The system follows a monolithic backend architecture with a clear separation of concerns, making it easy to maintain, test, and extend.

### 3.1 Technology Stack

    - Backend Framework: Spring Boot 3.x
    - Programming Language: Java 17
    - Database: MySQL (Relational Database)
    - ORM: Spring Data JPA with Hibernate
    - Build Tool: Maven
    - Deployment Platform: Railway
    - API Style: RESTful APIs
    - Testing Frameworks: JUnit 5, Mockito

The system follows a layered monolithic backend architecture, which is appropriate given the problem size and allows strong transactional guarantees.

                                  Client (Web UI)
                                        ↓
                        REST API Layer (Spring Boot Controllers)
                                        ↓
                      Service Layer (Business Logic & Transactions)
                                        ↓
                          Persistence Layer (Spring Data JPA)
                                        ↓
                             Relational Database (MySQL)

### 3.2 Design Pattern Used

    - Controller–Service–Repository Pattern
        This pattern ensures that HTTP request handling, business logic, and data access logic remain strictly separated.

    - Repository Pattern
        All database operations are abstracted through JPA repositories, which improves testability and prevents tight coupling with persistence logic.

    - Service Layer Pattern
        Business rules such as slot validation, booking rules, and availability checks are centralized in service classes.

    - DTO (Data Transfer Object) Pattern
        DTOs are used to transfer data between the client and server without exposing internal entity structures.

    - Exception Handling Pattern
        A centralized global exception handler ensures consistent error responses across all APIs.


## 4. Database Schema

The database schema is designed to support interview slot creation, booking, and candidate management while ensuring referential integrity and preventing inconsistent states.

### 4.1 Interview Slot Table

    Stores all interview slots created by interviewers.

        | Column Name      | Type      | Description                |
        | ---------------- | --------- | -------------------------- |
        | id               | BIGINT    | Primary key                |
        | interviewer_name | VARCHAR   | Name of interviewer        |
        | start_time       | TIMESTAMP | Interview start time (UTC) |
        | end_time         | TIMESTAMP | Interview end time (UTC)   |
        | status           | VARCHAR   | AVAILABLE / BOOKED         |
        | created_at       | TIMESTAMP | Slot creation time         |

### 4.2 Candidate Table

    Stores registered candidates.

        | Column Name | Type      | Description            |
        | ----------- | --------- | ---------------------- |
        | id          | BIGINT    | Primary key            |
        | name        | VARCHAR   | Candidate name         |
        | email       | VARCHAR   | Unique candidate email |
        | created_at  | TIMESTAMP | Registration timestamp |

### 4.3 Booking Table

    Represents a successful booking between a candidate and an interview slot.

        | Column Name  | Type      | Description                   |
        | ------------ | --------- | ----------------------------- |
        | id           | BIGINT    | Primary key                   |
        | candidate_id | BIGINT    | Foreign key to candidate      |
        | slot_id      | BIGINT    | Foreign key to interview slot |
        | booking_time | TIMESTAMP | Time of booking               |


## 5. Architecture

### 5.1 Full system architecture

                                  Client (Web UI)
                                        ↓
                        REST API Layer (Spring Boot Controllers)
                                        ↓
                      Service Layer (Business Logic & Transactions)
                                        ↓
                          Persistence Layer (Spring Data JPA)
                                        ↓
                             Relational Database (MySQL)

### 5.2 Slot Booking Flow

                    Candidate → Fetch Available Slots
                                → Select Slot
                                → Send Booking Request
                                        ↓
                                Backend Validation
                                        ↓
                            Slot Status Check (AVAILABLE?)
                                        ↓
                            YES → Mark Slot as BOOKED
                                        ↓
                                Create Booking Record
                                        ↓
                                Return Confirmation
                            NO  → Return 409 Conflict


## 6. Time Handling Strategy

    - The system uses timestamp-based scheduling to avoid time-zone-related issues.
    - All times are stored in UTC
    - Frontend converts local time to UTC before sending requests
    - Backend performs all comparisons using timestamps
    - Database stores TIMESTAMP values


## 7. Error Handling Strategy

### 7.1 Common Failure Scenarios

    - Attempt to book an already booked slot
    - Invalid date or time range
    - Candidate not found
    - Database connectivity failure

### 7.2 Global Exception Handling

    A centralized @ControllerAdvice handles all exceptions and returns structured error responses.

    {
        "timestamp": "2026-01-17T10:30:00",
        "status": 409,
        "error": "Conflict",
        "message": "Slot is already booked"
    }


## 8. Concurrency and Race Condition Handling

### 8.1 Problem: 

    Multiple candidates may attempt to book the same slot concurrently.
        - Slot status is checked inside a transaction
        - Database updates are atomic
        - Booking requests fail gracefully if the slot is no longer available

### 8.2 Solution: Optimistic Locking

    - Version is checked during update

    - Only one transaction succeeds

    @Transactional
    public Booking bookSlot(Long candidateId, Long slotId) {
        InterviewSlot slot = slotRepository.findById(slotId)
            .orElseThrow(...);

        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException();
        }

        slot.markBooked(); // version incremented
        slotRepository.save(slot);

        return bookingRepository.save(new Booking(candidateId, slotId));
    }

### 8.3 Why Optimistic Locking?

    - Low contention expected
    - Better throughput than pessimistic locking
    - Works well in horizontally scaled systems


## 9. UI Layout (Conceptual)

### 9.1 Interviewer View

    - Form to create interview slots
    - List of created slots

### 9.2 Candidate View

    - List of available slots
    - One-click booking button
    - Booking confirmation message


## 10. Scalability

    - The backend exposes stateless REST APIs, allowing the application to scale horizontally by adding more server instances.
    - Database indexes on frequently queried fields ensure that read and write operations remain efficient as data volume grows.
    - Pagination and filtering can be applied to listing APIs to prevent large result sets from impacting performance.


## 11. Future Improvements

    - Authentication and authorization can be added to restrict slot creation and booking actions based on user roles.
    - Email notifications can be integrated to automatically inform candidates and interviewers about booking, cancellation, and rescheduling events.
    - Rescheduling support can be implemented to allow candidates or interviewers to modify existing bookings without manual intervention.
    - Pagination can be introduced in the available slots API to efficiently handle large datasets as the number of interview slots increases.

