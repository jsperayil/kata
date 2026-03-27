# Step 1: Your First Traffic Light

## What You'll Learn

- Creating a REST API with Spring Boot
- Using enums to represent a fixed set of states
- Writing integration tests with MockMvc

## The Problem

Build an API that manages a single traffic light. The light can be **RED**, **YELLOW**, or **GREEN**.

You need two endpoints:

| Method | Path     | Description              |
|--------|----------|--------------------------|
| GET    | `/light` | Returns the current state |
| PUT    | `/light` | Changes the state         |

The light should start as **RED** by default.

## Your Task

1. Look at `LightState.java` — this enum defines the three possible states
2. Look at `TrafficLight.java` — this is your domain model holding the current state
3. Open `TrafficLightController.java` — this is the REST controller you need to understand
4. Run the tests: `mvn test`
5. Try it yourself: `mvn spring-boot:run`, then in another terminal:

```bash
# Check the current state
curl http://localhost:8080/light

# Change to green
curl -X PUT http://localhost:8080/light \
  -H "Content-Type: application/json" \
  -d '{"state": "GREEN"}'

# Check it changed
curl http://localhost:8080/light
```

## Key Concepts

### REST (Representational State Transfer)

REST is a way to design APIs around **resources**. Here, our resource is a traffic light.

- **GET** = read the current state (no side effects)
- **PUT** = update the state (replaces the current value)

### Enums

An enum is a type with a fixed set of values. A traffic light can only be RED, YELLOW, or GREEN — never "BLUE" or "MAYBE". Enums enforce this at compile time.

### `@RestController`

This Spring annotation tells the framework: "this class handles HTTP requests and returns JSON responses." Each method annotated with `@GetMapping` or `@PutMapping` handles a specific HTTP method.

## What's Next

In **Step 2**, we'll add rules: a light can only transition in a valid sequence (GREEN -> YELLOW -> RED -> GREEN). Right now, you can jump from RED straight to YELLOW — that's not how real traffic lights work.
