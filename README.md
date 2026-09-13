# Task Manager Application

A RESTful Task Manager backend built with **Java, Spring Boot, Spring Security, JWT, JPA/Hibernate, MySQL, Redis, and Docker**.

The application provides user authentication and allows authenticated users to create, view, update, complete, and delete their own tasks.

A major focus of the application is **authentication, authorization, task ownership, layered architecture, DTO-based API design, exception handling, database persistence, and caching**.

---

## Features

- User registration
- User login
- JWT-based authentication
- BCrypt password hashing
- Protected REST APIs
- User-specific task ownership
- Create tasks
- Retrieve all tasks belonging to the authenticated user
- Retrieve an individual task
- Update tasks
- Mark tasks as completed
- Delete completed tasks
- Prevention of editing completed tasks
- Prevention of deleting incomplete tasks
- Custom exception handling
- DTO-based request and response handling
- MySQL database persistence
- Redis caching for individual task retrieval
- Dockerized backend

---

# Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 3.5 | Backend framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Persistent database |
| Redis | Task caching |
| Maven | Build and dependency management |
| Docker | Application containerization |
| Lombok | Reducing boilerplate code |

---

# Architecture

The application follows a layered architecture:

```text
                    Client
                      |
                      | HTTP Request
                      v
              ------------------
              |   Controller   |
              ------------------
                      |
                      v
              ------------------
              |    Service     |
              ------------------
                 /          \
                /            \
               v              v
       -----------------   -----------------
       |  Repository   |   | Redis Service |
       -----------------   -----------------
               |
               v
       -----------------
       |     MySQL     |
       -----------------

````
# Request Flow

````text

HTTP Request
     |
     v
Controller
     |
     v
Service
     |
     |----> Redis
     |
     |----> Repository
                |
                v
              MySQL

````
- Controllers are responsible for handling HTTP requests and responses.
- Services contain the application's business logic.
- Repositories communicate with the database through Spring Data JPA.
- Redis is used as a caching layer for individual task retrieval.
---
# Authentication
The application uses Spring Security and JWT for stateless authentication.

**Registration**

A user provides:
- Name
- Username
- Email
- Password
  
The password is encoded using BCrypt before the user is stored in the database.

```text
User
 |
 | username + password
 v
UserService
 |
 | BCrypt encode
 v
MySQL
```
**Login**

A user provides:
- Username
- Password

The login flow is:
```text
Client
   |
   | username + password
   v
AuthenticationManager
   |
   v
CustomUserDetailService
   |
   v
UserRepository
   |
   v
MySQL
   |
   v
Password verification
   |
   v
JWT generated
   |
   v
Client
```

After successful authentication, the backend generates a JWT containing the authenticated user's username.

The token is then used for subsequent protected requests.

---

# Java Authentication Flow

For protected endpoints, the client sends:

```text
Authorization: Bearer <JWT>
```

**The custom JWT filter:**

- Reads the Authorization header.
- Extracts the JWT.
- Extracts the username from the token.
- Loads the corresponding user.
- Validates the token.
- Creates an authenticated UsernamePasswordAuthenticationToken.
- Stores the authentication in Spring Security's SecurityContext.

```text
HTTP Request
     |
     v
JWTFilter
     |
     v
Extract JWT
     |
     v
Extract username
     |
     v
Load User
     |
     v
Validate JWT
     |
     v
SecurityContext
     |
     v
Controller / Service
```

The application uses stateless sessions.

---

# Authorization and Task Ownership
Each task belongs to a user.

The relationship is represented in the Task entity:

```text 
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

This creates the following relationship:

```text 
User
 |
 |---- Task
 |
 |---- Task
 |
 |---- Task
```

A user can have multiple tasks, while each task belongs to one user.

The authenticated username is obtained from Spring Security:

```text
SecurityContextHolder.getContext()
```
The application then uses that username when retrieving or modifying tasks.

For example:
```text
taskRepo.findByIdAndUserUsername(id, username)
```
This means a user cannot simply change a task ID in the URL and access another user's task.

The task must belong to the authenticated user.

# Task Operations

### Create Task

```task
POST /task
```
The authenticated user is obtained from the security context.

The task is then associated with that user before being saved.

```text
JWT
 |
 v
Authenticated User
 |
 v
Create Task
 |
 v
task.setUser(user)
 |
 v
MySQL
```
<br>

### Get All tasks
```text
GET /task
```

Returns tasks belonging to the currently authenticated user.

The repository method used is:

```text
findByUserUsername(String username)
```
<br>

### Get Task By Id

```text
GET /task/{id}
```
The application first checks Redis.

```text
GET /task/{id}
       |
       v
     Redis
     /   \
   Hit   Miss
   |       |
   |       v
   |     MySQL
   |       |
   |       v
   |     Redis
   |       |
   +-------+
       |
       v
    Response
```

The database remains the persistent source of task data while Redis is used as a cache.

<br>

### Update Task

```text
PUT /task/{id}
```

A task can only be updated if:

- It exists.
- It belongs to the authenticated user.
- It has not already been completed.

Attempting to edit a completed task results in a custom exception.

<br>

### Update Completion Status
```text
PATCH /task/{id}
```
Updates the completion status of a task.

The task must belong to the authenticated user.

<br>

### Delete Task
```text
DELETE /task/{id}
```

A task can only be deleted if:

- It exists.
- It belongs to the authenticated user.
- It has already been completed.

The corresponding Redis cache entry is also removed after deletion.

<br>

## Rest API

### User APIs

### Signup

```text
POST /user/signup
```
Creates a new user account.

### Login
```text
POST /user/login
```
Authenticates a user and returns a JWT.

### Task APIs
| Method | Endpoint	| Description |
| ------ | -------- | ----------- |
|GET |	/task	| Get authenticated user's tasks |
|GET	| /task/{id} | Get a specific task
|POST	| /task |	Create a task
|PUT | /task/{id} |	Update a task
|PATCH	| /task/{id} | Update task completion status
|DELETE | /task/{id} | Delete a completed task

Task endpoints require authentication.

### DTO Design

The application uses separate DTOs instead of directly exposing JPA entities through the API.

Example include:
```text
SignupDTO
SignupResponseDTO

LoginDTO
LoginResponseDTO

TaskRequestDTO
TaskResponseDTO
UpdateTaskDTO
TaskCompletionDTO
```
The general flow

```text
Client
  |
  v
Request DTO
  |
  v
Service
  |
  v
Entity
  |
  v
Repository
  |
  v
MySQL
```

For response

```text
MySQL
  |
  v
Entity
  |
  v
Response DTO
  |
  v
Client
```

This keeps the API representation separate from the persistence entities.

### Redis Caching

Redis is used to cache individual task responses.

The cache key follows the structure:

```text
task:<username>:<taskId>
```

For example:

```text
task:ram:15
```
When retrieving a task by ID:

1. The application checks Redis.
2. If the task exists in Redis, the cached response is returned.
3. If it is not cached, the application retrieves the task from MySQL.
4. The response is stored in Redis.
5. The response is returned to the client.

When a task is updated, the cache is updated.

When a task is deleted, the corresponding cache entry is deleted.

Redis failures are handled inside TaskRedisService, allowing the application to fall back to the database.

### Exception Handling

The application uses custom exceptions for different business cases.

Examples include:

```text
TaskNotFoundException
UserNotFoundException
UserAlreadyExistException
EmailAlreadyExistsException
InvalidPasswordException
EditCompletedTaskException
UncompletedTaskDeletionException
```

A centralized GlobalExceptionHandler handles application exceptions and converts them into appropriate HTTP responses.

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/example/Task/Manager/Application/
    │       │
    │       ├── configuration/
    │       │   |--- CorsConfig.java
    │       │   |--- RedisConfig.java
    │       │   |--- SecurityConfig.java
    │       │
    │       ├── controller/
    │       │   |--- TaskController.java
    │       │   |--- UserController.java
    │       │
    │       ├── dto/
    │       │   |--- LoginDTO.java
    │       │   |--- LoginResponseDTO.java
    │       │   |--- SignupDTO.java
    │       │   |--- SignupResponseDTO.java
    │       │   |--- TaskCompletionDTO.java
    │       │   |--- TaskRequestDTO.java
    │       │   |--- TaskResponseDTO.java
    │       │   |--- UpdateTaskDTO.java
    │       │
    │       ├── entity/
    │       │   |--- Task.java
    │       │   |--- User.java
    │       │
    │       ├── exceptionHandler/
    │       │   |--- EditCompletedTaskException.java
    │       │   |--- EmailAlreadyExistsException.java
    │       │   |--- GlobalExceptionHandler.java
    │       │   |--- InvalidPasswordException.java
    │       │   |--- TaskNotFoundException.java
    |       |   |--- UncompletedTaskDeletionException.java
    |       |   |--- UserAlreadyExistException.java
    |       |   |--- UserNotFoundException.java
    |       |
    │       ├── repository/
    │       │   |--- TaskRepo.java
    │       │   |--- UserRepo.java
    │       │
    │       ├── security/
    │       │   |--- JWTFilter.java
    │       │   |--- JWTService.java
    │       │
    │       ├── service/
    │       │   |--- CustomUserDetailService.java
    │       │   |--- TaskRedisService.java
    │       │   |--- TaskService.java
    │       │   |--- UserService.java
    │       │
    │       └── TaskManagerApplication.java
    │
    └── resources/
        └── application.properties
        └── application-local.properties
        └── application-prod.properties
```

## Database Model
### User
The User entity contains:

```text
id
name
username
email
password
createdAt
role
```

The username and email are unique.

Users implement Spring Security's UserDetails interface.

### Task

The Task entity contains:
```text
id
title
description
createdAt
editedAt
completed
user_id
```
user_id is a foreign key representing the owner of the task.

### Configuration
The application uses different Spring profiles for different environments.

The local profile connects to MySQL running on:
```text
localhost:3306
```
with the database

```text
task_manager
```
The local database password is supplied through:

```text
LOCAL_DB_PASSWORD
```
The JWT signing secret is also supplied through an environment variable:

```text
JWT_SECRET
```
## Running Locally

### Prerequisites
Install:
-- Java 21
-- MySQL
-- Maven
-- Redis

### Create the Database
Create a MySQL database:
```text
CREATE DATABASE task_manager;
```

### Configure Environment Variables
Set:
```text
LOCAL_DB_PASSWORD=your_mysql_password
JWT_SECRET=your_secret_key
```

The JWT secret should be sufficiently long for the signing algorithm used by the application.

### Run the application

Using maven wrapper on windows:
```text
.\mvnw.cmd spring-boot:run
```

Or using maven:
```text
mvn spring-boot:run
```

The application runs on
```text
http://localhost:8080
```

## Docker 

The project contains a multi-stage Dockerfile

### Build Stage
The first stage uses Maven and Java 21 to build the Spring Boot application.

```text
Maven
  |
  v
Compile
  |
  v
Package
  |
  v
JAR
```

### Runtime Stage
The second stage uses a Java 21 JRE image and runs the generated JAR.

```text
Java 21 JRE
    |
    v
Spring Boot JAR
    |
    v
Application
```

Build the Docker image:

```text
docker build -t task-manager .
```

Run the container:

```text
docker run -p 8080:8080 task-manager
```

The Dockerfile makes the backend reproducible across environments that support Docker.

### Security Considerations
-- The application implements:
-- BCrypt password hashing
-- JWT-based authentication
-- Stateless Spring Security sessions
-- Protected task endpoints
-- User-specific task ownership
-- Server-side ownership checks
-- Environment variables for sensitive configuration
-- DTOs to control API data exposure

The application does not rely on a client-provided user ID to determine task ownership.

Instead, ownership is derived from the authenticated user.

### What I Learned From This Project
This project was built to understand how a real backend application works beyond simply creating CRUD endpoints.

Key concepts implemented include:

-- Layered architecture
-- REST API development
-- Authentication
-- Authorization
-- JWT
-- Spring Security filter chain
-- SecurityContext
-- Password hashing
-- JPA entity relationships
-- Database foreign keys
-- DTO pattern
-- Repository pattern
-- Service layer business logic
-- Exception handling
-- Redis caching
-- Environment-based configuration
-- Docker containerization
-- Frontend-backend communication

### Future Improvements

Potential improvements include:
-- Automated unit and integration tests
-- API documentation using OpenAPI / Swagger
-- Refresh tokens
-- Pagination
-- Task priorities
-- Due dates
-- Search and filtering
-- Rate limiting
-- Improved cache invalidation strategies
-- CI/CD pipeline
-- Application monitoring
-- Production deployment

## Author
### Lokesh Yadav
Github: 
```text 
https://github.com/LokeshKumarYadav1
```
