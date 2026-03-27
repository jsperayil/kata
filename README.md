# Step 6: Pause and Resume

## What You'll Learn

- Using POST for actions that aren't CRUD operations
- Separating scheduling from logic for testability
- Constructor injection with multiple dependencies
- Testing stateful service behavior

## The Problem

The automatic cycle from Step 5 runs forever. Sometimes you need to stop it — for maintenance, emergencies, or manual control. You need:

| Method | Path                     | Description                              |
|--------|--------------------------|------------------------------------------|
| POST   | `/intersection/pause`    | Stops the automatic cycle                |
| POST   | `/intersection/resume`   | Restarts the automatic cycle             |
| GET    | `/intersection/status`   | Returns whether the cycle is running     |

Manual PUT transitions should still work while paused — an operator might need to manually control the lights.

## Key Concepts

### POST for Actions

GET reads state. PUT updates a resource. But "pause" and "resume" are **actions**, not resources. POST is the right choice — it triggers a side effect without representing a specific resource.

### Separating Scheduling from Logic

`advanceCycle()` contains the transition logic. `scheduleNextPhase()` handles timing. Pause only stops scheduling — it doesn't affect the logic itself. This separation means:

- **In production:** the scheduler calls `advanceCycle()` automatically
- **When paused:** the scheduler stops, but manual transitions still work
- **In tests:** we call `advanceCycle()` directly without waiting for real timers

This is a useful pattern: separate *what* happens from *when* it happens.

### Constructor Injection with Multiple Dependencies

`IntersectionController` now receives both `Intersection` and `TrafficCycleService`:

```java
public IntersectionController(Intersection intersection, TrafficCycleService cycleService) {
    this.intersection = intersection;
    this.cycleService = cycleService;
}
```

Spring resolves both beans automatically. No `@Autowired` needed — when there's only one constructor, Spring uses it by default.

## Your Task

1. Look at `TrafficCycleService.java` — see `pause()` and `resume()` methods
2. Look at `IntersectionController.java` — the new POST endpoints and `StatusResponse`
3. Run the tests: `mvn test` (21 tests)
4. Try it:

```bash
mvn spring-boot:run

# Check status
curl http://localhost:8080/intersection/status

# Pause the cycle
curl -X POST http://localhost:8080/intersection/pause

# Manual transition while paused
curl -X PUT http://localhost:8080/intersection/NORTH_SOUTH \
  -H "Content-Type: application/json" -d '{"state": "GREEN"}'

# Resume
curl -X POST http://localhost:8080/intersection/resume
```

## What's Next

In **Step 7**, we'll add history tracking — every state change gets recorded with a timestamp so you can query what happened and when.
