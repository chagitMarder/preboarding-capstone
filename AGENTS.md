This is a project designed for task management.

## Build and Run

to run tests use:
```bash
./mvnw.cmd test
```

To start the Spring Boot application, run:

```bash
./mvnw.cmd spring-boot:run
```

The application will be available at:

```text
http://localhost:8080
```

## rules
1. Controller layer: Controllers handle HTTP requests and responses only. Business logic should be implemented in service classes.
2. Code organization: Keep classes focused on a single responsibility.
3. Testing: Write tests for both successful operations and error scenarios.
4. run the tests before saying you're done
