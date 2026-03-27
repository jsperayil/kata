# Traffic Light Controller API — Step-by-Step Guide

A hands-on kata for learning API design, state machines, and concurrency with Spring Boot.

## Prerequisites

- Java 17+
- Maven 3.8+
- A terminal and a text editor

## How This Kata Works

You'll build a traffic light controller API one step at a time. Each step introduces one new concept by extending the code you already have. Solution branches are available if you get stuck.

| Step | Branch | Concept |
|------|--------|---------|
| 1 | `step-1-single-light` | REST basics — GET and PUT for a single light |
| 2 | `step-2-state-machine` | Valid transitions only (GREEN -> YELLOW -> RED -> GREEN) |
| 3 | `step-3-intersection` | Multiple directions (N/S, E/W) at one intersection |
| 4 | `step-4-conflict-safety` | Never allow conflicting directions to be green at the same time |
| 5 | `step-5-automatic-timing` | Scheduled automatic light changes |
| 6 | `step-6-pause-resume` | Pause, resume, and override the cycle |
| 7 | `step-7-history` | Track and query all state changes |
| 8 | `step-8-multiple-intersections` | Scale to multiple intersections |

---

## Step 1: Your First Traffic Light

**Goal:** Build a REST API that manages a single traffic light with GET and PUT endpoints.

**What you'll learn:**
- How Spring Boot handles HTTP requests via `@RestController`
- Using enums (`LightState`) to model a constrained set of values
- Writing integration tests with `MockMvc`

**Files to explore:**
- `src/main/java/com/kata/trafficlight/LightState.java` — the state enum
- `src/main/java/com/kata/trafficlight/TrafficLight.java` — the domain model
- `src/main/java/com/kata/trafficlight/TrafficLightController.java` — the REST controller
- `src/test/java/com/kata/trafficlight/TrafficLightControllerTest.java` — the tests

**Run it:**
```bash
mvn test              # run the tests
mvn spring-boot:run   # start the server on port 8080
```

**Try it:**
```bash
curl http://localhost:8080/light
curl -X PUT http://localhost:8080/light -H "Content-Type: application/json" -d '{"state": "GREEN"}'
```

See [the Step 1 README](../README.md) for the full walkthrough.
