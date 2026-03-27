# Step 2: State Machine — Valid Transitions Only

## What You'll Learn

- Modeling allowed state transitions inside an enum
- Using a switch expression (Java 17+)
- Throwing and handling custom exceptions
- Testing both happy paths and error cases

## The Problem

Right now you can change a traffic light from RED straight to YELLOW. Real traffic lights follow a strict cycle:

```
RED → GREEN → YELLOW → RED → GREEN → ...
```

Any other transition (e.g., RED → YELLOW, GREEN → RED) should be **rejected with a 400 Bad Request**.

## Key Concepts

### State Machine

A state machine has a fixed set of states and a fixed set of **allowed transitions** between them. Our traffic light has 3 states and 3 transitions:

```
RED ──→ GREEN ──→ YELLOW ──→ RED
```

The `canTransitionTo()` method on `LightState` enforces this. If you try an invalid transition, you get an `InvalidTransitionException`.

### Switch Expressions

Java 17 lets you write exhaustive switches that return values:

```java
return switch (this) {
    case RED -> GREEN;
    case GREEN -> YELLOW;
    case YELLOW -> RED;
};
```

The compiler ensures every case is covered — if someone adds a new enum value, this won't compile until the switch is updated.

### Custom Exceptions

`InvalidTransitionException` is a domain-specific error. It carries meaning: "this transition violates the rules." The `@ExceptionHandler` in the controller converts it to a 400 response with a clear error message.

## Your Task

1. Look at `LightState.java` — see how `getNext()` and `canTransitionTo()` work
2. Look at `TrafficLight.java` — `transitionTo()` replaces the old `setState()`
3. Look at `TrafficLightController.java` — see the `@ExceptionHandler`
4. Run the tests: `mvn test` (10 tests, covering all valid and invalid transitions)
5. Try it:

```bash
mvn spring-boot:run

# Valid: RED → GREEN
curl -X PUT http://localhost:8080/light -H "Content-Type: application/json" -d '{"state": "GREEN"}'

# Invalid: GREEN → RED (should return 400)
curl -X PUT http://localhost:8080/light -H "Content-Type: application/json" -d '{"state": "RED"}'
```

## What's Next

In **Step 3**, we'll model a full intersection — multiple directions (N/S, E/W) each with their own traffic light.
