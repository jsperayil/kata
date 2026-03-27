# Step 3: Intersection — Multiple Directions

## What You'll Learn

- Domain modeling — grouping related objects into a meaningful whole
- `EnumMap` for efficient enum-keyed collections
- Path variables in REST endpoints (`/intersection/{direction}`)
- Testing independent state across multiple components

## The Problem

A real intersection has traffic lights for multiple directions. We need to model two direction groups — **NORTH_SOUTH** and **EAST_WEST** — each with their own independent traffic light.

The old `/light` endpoint is gone. Now:

| Method | Path                           | Description                          |
|--------|--------------------------------|--------------------------------------|
| GET    | `/intersection`                | Returns the state of all directions  |
| PUT    | `/intersection/{direction}`    | Transitions one direction's light    |

## Key Concepts

### Domain Modeling

Instead of one loose `TrafficLight`, we now have an `Intersection` that **owns** a collection of lights. This is composition — the `Intersection` is responsible for its lights, and the outside world talks to the intersection, not individual lights directly.

```
Intersection
├── NORTH_SOUTH → TrafficLight (RED)
└── EAST_WEST   → TrafficLight (RED)
```

### EnumMap

When your keys are enum values, `EnumMap` is the right choice. It's backed by an array internally — fast and memory-efficient. It also guarantees iteration in enum declaration order.

### Path Variables

`@PathVariable Direction direction` tells Spring to parse the URL segment and convert it to a `Direction` enum. Spring handles the string-to-enum conversion automatically:

```
PUT /intersection/NORTH_SOUTH  →  Direction.NORTH_SOUTH
```

## Your Task

1. Look at `Direction.java` — a simple enum with two values
2. Look at `Intersection.java` — see how it initializes and manages lights per direction
3. Look at `IntersectionController.java` — note `@PathVariable` and how it replaces the old controller
4. Run the tests: `mvn test` (6 tests)
5. Try it:

```bash
mvn spring-boot:run

# See all directions
curl http://localhost:8080/intersection

# Change NORTH_SOUTH to green
curl -X PUT http://localhost:8080/intersection/NORTH_SOUTH \
  -H "Content-Type: application/json" -d '{"state": "GREEN"}'

# Verify EAST_WEST is still red
curl http://localhost:8080/intersection
```

## What's Next

In **Step 4**, we'll add the critical safety rule: conflicting directions (N/S and E/W) must never be green at the same time.
