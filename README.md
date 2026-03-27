# Step 7: History — Tracking State Changes

## What You'll Learn

- Java records for immutable data
- Audit logging — recording what happened and when
- Query parameters (`@RequestParam`) for filtering
- `Collections.unmodifiableList()` for defensive copying
- Recording only on success (not on failed transitions)

## The Problem

When something goes wrong at an intersection, you need to know what happened. Every state change should be recorded with:

- Which direction changed
- What it changed from and to
- When it happened

You also need to query this history — all events, or filtered by direction.

| Method | Path                                         | Description                        |
|--------|----------------------------------------------|------------------------------------|
| GET    | `/intersection/history`                      | Returns all state change events    |
| GET    | `/intersection/history?direction=NORTH_SOUTH`| Returns events for one direction   |

## Key Concepts

### Java Records

`StateChangeEvent` is a record — an immutable data class with automatic `equals()`, `hashCode()`, and `toString()`:

```java
public record StateChangeEvent(
        Direction direction,
        LightState fromState,
        LightState toState,
        Instant timestamp
) {}
```

Records are ideal for events — once something happened, its details don't change.

### Audit Logging Pattern

History is recorded **inside the `transition()` method**, after the transition succeeds but as part of the same operation:

```java
LightState previousState = getLight(direction).getState();
getLight(direction).transitionTo(newState);        // may throw
history.add(new StateChangeEvent(...));             // only runs on success
```

If the transition throws (invalid or conflict), no event is recorded. This guarantees the history only contains real transitions.

### Query Parameters

`@RequestParam(required = false) Direction direction` makes the parameter optional:

```
GET /intersection/history              → all events
GET /intersection/history?direction=EAST_WEST  → filtered
```

Spring converts the string to a `Direction` enum automatically, just like path variables.

## Your Task

1. Look at `StateChangeEvent.java` — a record with a convenience constructor that stamps the current time
2. Look at `Intersection.java` — see where history is recorded and the two `getHistory()` methods
3. Look at `IntersectionController.java` — the `@RequestParam` endpoint
4. Run the tests: `mvn test` (26 tests)
5. Try it:

```bash
mvn spring-boot:run

# Wait a few seconds for some automatic transitions, then:
curl http://localhost:8080/intersection/history

# Filter by direction
curl "http://localhost:8080/intersection/history?direction=NORTH_SOUTH"
```

## What's Next

In **Step 8**, we'll scale to multiple intersections — each managed independently with its own cycle and history.
