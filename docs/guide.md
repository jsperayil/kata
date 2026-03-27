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
- Writing unit tests

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

---

## Step 2: State Machine — Valid Transitions Only

**Goal:** Enforce that lights can only transition in the correct sequence: RED → GREEN → YELLOW → RED.

**What you'll learn:**
- Modeling allowed transitions in an enum with `canTransitionTo()`
- Switch expressions (Java 17+)
- Custom exceptions and `@ExceptionHandler` for 400 responses
- Testing both valid and invalid paths

**New/changed files:**
- `src/main/java/com/kata/trafficlight/LightState.java` — added `getNext()` and `canTransitionTo()`
- `src/main/java/com/kata/trafficlight/TrafficLight.java` — `transitionTo()` replaces `setState()`
- `src/main/java/com/kata/trafficlight/InvalidTransitionException.java` — domain exception
- `src/main/java/com/kata/trafficlight/TrafficLightController.java` — `@ExceptionHandler`

**Run it:**
```bash
mvn test              # 10 tests covering all valid and invalid transitions
mvn spring-boot:run   # try valid and invalid transitions via curl
```

---

## Step 3: Intersection — Multiple Directions

**Goal:** Model a full intersection with NORTH_SOUTH and EAST_WEST directions, each with their own traffic light.

**What you'll learn:**
- Domain modeling with composition (`Intersection` owns `TrafficLight`s)
- `EnumMap` for enum-keyed collections
- Path variables (`@PathVariable`) in REST endpoints

**New/changed files:**
- `src/main/java/com/kata/trafficlight/Direction.java` — direction enum
- `src/main/java/com/kata/trafficlight/Intersection.java` — intersection model
- `src/main/java/com/kata/trafficlight/IntersectionController.java` — replaces `TrafficLightController`

**Endpoints:**
- `GET /intersection` — returns state of all directions
- `PUT /intersection/{direction}` — transitions one direction's light

**Run it:**
```bash
mvn test              # 6 tests for multi-direction behavior
mvn spring-boot:run   # try changing directions independently via curl
```
