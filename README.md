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

## What's Next — Step 8: Multiple Intersections

The final step scales the system to manage multiple intersections, each independently controlled.

### Concepts to Explore

- **Registry pattern** — an `IntersectionRegistry` that stores intersections by ID, with CRUD operations to create and remove them
- **Nested REST resources** — URLs like `/intersections/{id}/history`, `/intersections/{id}/pause` to scope actions to a specific intersection
- **Lifecycle management** — each intersection gets its own `TrafficCycleService` instance, started on creation and stopped on removal
- **Resource not found** — returning 404 when an intersection ID doesn't exist
- **Dynamic bean management** — creating and destroying service instances at runtime rather than at startup

### Suggested Endpoints

| Method | Path                                  | Description                        |
|--------|---------------------------------------|------------------------------------|
| POST   | `/intersections`                      | Create a new intersection          |
| GET    | `/intersections`                      | List all intersection IDs          |
| GET    | `/intersections/{id}`                 | Get state of one intersection      |
| DELETE | `/intersections/{id}`                 | Remove an intersection             |
| PUT    | `/intersections/{id}/{direction}`     | Transition a direction's light     |
| GET    | `/intersections/{id}/history`         | Get history for one intersection   |
| POST   | `/intersections/{id}/pause`           | Pause one intersection's cycle     |
| POST   | `/intersections/{id}/resume`          | Resume one intersection's cycle    |

---

## Bonus Challenge: Four Directions Instead of Two

The current system groups opposing lanes into two directions (`NORTH_SOUTH`, `EAST_WEST`). A more realistic model uses four individual directions, each with its own light.

### What Changes

**1. Expand the `Direction` enum:**

```java
public enum Direction {
    NORTH, SOUTH, EAST, WEST
}
```

**2. Define conflict groups:**

Opposing directions can be green together (NORTH + SOUTH share a green phase), but perpendicular directions cannot. Introduce a conflict mapping:

```java
private static final Map<Direction, Set<Direction>> CONFLICTS = Map.of(
    Direction.NORTH, Set.of(Direction.EAST, Direction.WEST),
    Direction.SOUTH, Set.of(Direction.EAST, Direction.WEST),
    Direction.EAST,  Set.of(Direction.NORTH, Direction.SOUTH),
    Direction.WEST,  Set.of(Direction.NORTH, Direction.SOUTH)
);
```

**3. Update `checkForConflicts()`:**

Instead of checking all *other* directions, only check the ones in the conflict set:

```java
private void checkForConflicts(Direction requested) {
    for (Direction conflicting : CONFLICTS.get(requested)) {
        LightState state = getLight(conflicting).getState();
        if (state == LightState.GREEN || state == LightState.YELLOW) {
            throw new ConflictException(requested, conflicting);
        }
    }
}
```

**4. Update `TrafficCycleService`:**

Instead of cycling one direction at a time, cycle **paired directions** together. One approach: define phase groups:

```java
private static final List<Set<Direction>> PHASES = List.of(
    Set.of(Direction.NORTH, Direction.SOUTH),
    Set.of(Direction.EAST, Direction.WEST)
);
```

Each phase transitions its group through GREEN → YELLOW → RED together before moving to the next phase.

### Concepts This Teaches

- **Conflict graphs** — relationships between entities that constrain behavior
- **Grouping and phases** — coordinating multiple objects through shared state transitions
- **Refactoring enums** — how a simple enum change ripples through the domain
