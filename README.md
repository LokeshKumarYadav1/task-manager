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

**Create Task**

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

**Get All tasks**
```text
GET /task
```

Returns tasks belonging to the currently authenticated user.

The repository method used is:

```text
findByUserUsername(String username)
```
<br>

**Get Task By Id**

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

**Update Task**

```text
PUT /task/{id}
```

A task can only be updated if:

- It exists.
- It belongs to the authenticated user.
- It has not already been completed.

Attempting to edit a completed task results in a custom exception.

<br>

**Update Completion Status**
```text
PATCH /task/{id}
```
Updates the completion status of a task.

The task must belong to the authenticated user.

<br>

**Delete Task**
```text
DELETE /task/{id}
```

A task can only be deleted if:

- It exists.
- It belongs to the authenticated user.
- It has already been completed.

The corresponding Redis cache entry is also removed after deletion.

<br>
