# Step 5: Automatic Timing

## What You'll Learn

- Scheduling recurring work with `ScheduledExecutorService`
- Spring lifecycle hooks (`@PostConstruct`, `@PreDestroy`)
- Externalizing configuration with `@Value`
- Making a class a shared Spring bean (`@Component`)
- Testing time-based logic without actually waiting

## The Problem

Until now, you had to manually PUT every transition. Real traffic lights cycle automatically:

```
NORTH_SOUTH: RED → GREEN (10s) → YELLOW (3s) → RED
EAST_WEST:   RED → GREEN (10s) → YELLOW (3s) → RED
NORTH_SOUTH: RED → GREEN (10s) → ...
```

Each direction gets a turn. Green lasts 10 seconds, yellow lasts 3, then it switches to the other direction.

## Key Concepts

### ScheduledExecutorService

Java's built-in way to run code after a delay. We use `schedule()` to queue the next phase transition:

```java
scheduler.schedule(this::advanceCycle, delaySeconds, TimeUnit.SECONDS);
```

This is more flexible than `@Scheduled` (Spring's annotation) because each phase has a different delay.

### Spring Lifecycle Hooks

- `@PostConstruct` — runs after the bean is created and dependencies injected. We use it to kick off the first cycle.
- `@PreDestroy` — runs when the application shuts down. We use it to stop the scheduler cleanly.

### Shared State with @Component

`Intersection` is now a `@Component` — Spring creates one instance and injects it into both `IntersectionController` and `TrafficCycleService`. They share the same intersection.

### Externalizing Configuration

Timing values are in `application.properties`:

```properties
traffic.green.duration=10
traffic.yellow.duration=3
```

`@Value("${traffic.green.duration:10}")` reads the property, defaulting to 10 if missing. Change the config, restart — no code changes needed.

### Testing Without Timers

The tests call `advanceCycle()` directly instead of waiting for real timers. This makes tests fast and deterministic. The scheduling is just delivery — the logic is in `advanceCycle()`.

## Your Task

1. Look at `TrafficCycleService.java` — follow the `advanceCycle()` switch statement
2. Look at `Intersection.java` — it's now `@Component`
3. Look at `IntersectionController.java` — it receives `Intersection` via constructor injection
4. Look at `application.properties` — the timing configuration
5. Run the tests: `mvn test` (15 tests)
6. Try it:

```bash
mvn spring-boot:run

# Watch the lights cycle automatically
curl http://localhost:8080/intersection
# Wait 10 seconds...
curl http://localhost:8080/intersection
# Wait 3 more seconds...
curl http://localhost:8080/intersection
```

## What's Next

In **Step 6**, we'll add pause and resume commands — so you can stop the automatic cycle, and start it again.
