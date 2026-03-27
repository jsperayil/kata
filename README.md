# Step 4: Conflict Safety

## What You'll Learn

- Enforcing business rules that span multiple objects
- Choosing the right HTTP status code (409 Conflict)
- Testing safety invariants — proving dangerous states can't happen

## The Problem

Right now, nothing stops you from turning both NORTH_SOUTH and EAST_WEST green at the same time. In a real intersection, that causes crashes.

**The safety rule:** A direction cannot go GREEN if any other direction is GREEN or YELLOW.

A direction must complete its full cycle back to RED before the other direction can go GREEN:

```
1. NORTH_SOUTH: RED → GREEN → YELLOW → RED
2. EAST_WEST:   RED → GREEN  ✓  (now safe, NORTH_SOUTH is RED)
```

Attempting to violate this returns **409 Conflict**.

## Key Concepts

### Business Rules vs. State Rules

Step 2 added **state rules**: a single light can only follow RED → GREEN → YELLOW → RED. That's about one object's internal consistency.

Step 4 adds a **business rule**: the relationship between multiple objects must be valid. The `Intersection` is the right place for this rule because it's the only class that sees all directions.

### 409 Conflict

HTTP 409 means "your request is valid in form, but conflicts with the current state of the system." It's different from 400 Bad Request (malformed input). Here, the JSON is fine — the problem is *when* you're asking.

### Check-then-Act

The `transition()` method checks for conflicts *before* changing state. This ensures we never enter an unsafe state, even briefly. The pattern is:

```java
if (newState == GREEN) {
    checkForConflicts(direction);  // throws if unsafe
}
light.transitionTo(newState);      // only runs if safe
```

## Your Task

1. Look at `Intersection.java` — see `checkForConflicts()` and how it's called before the transition
2. Look at `ConflictException.java` — a domain exception for safety violations
3. Look at `IntersectionController.java` — the new `@ExceptionHandler` returning 409
4. Run the tests: `mvn test` (9 tests)
5. Try it:

```bash
mvn spring-boot:run

# Turn NORTH_SOUTH green
curl -X PUT http://localhost:8080/intersection/NORTH_SOUTH \
  -H "Content-Type: application/json" -d '{"state": "GREEN"}'

# Try EAST_WEST green — should get 409
curl -X PUT http://localhost:8080/intersection/EAST_WEST \
  -H "Content-Type: application/json" -d '{"state": "GREEN"}'

# Cycle NORTH_SOUTH back to RED
curl -X PUT http://localhost:8080/intersection/NORTH_SOUTH \
  -H "Content-Type: application/json" -d '{"state": "YELLOW"}'
curl -X PUT http://localhost:8080/intersection/NORTH_SOUTH \
  -H "Content-Type: application/json" -d '{"state": "RED"}'

# Now EAST_WEST can go green
curl -X PUT http://localhost:8080/intersection/EAST_WEST \
  -H "Content-Type: application/json" -d '{"state": "GREEN"}'
```

## What's Next

In **Step 5**, we'll add automatic timing — the lights will cycle on their own using a scheduled task, so you don't have to manually PUT each transition.
