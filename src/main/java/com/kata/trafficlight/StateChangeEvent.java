package com.kata.trafficlight;

import java.time.Instant;

public record StateChangeEvent(
        Direction direction,
        LightState fromState,
        LightState toState,
        Instant timestamp
) {
    public StateChangeEvent(Direction direction, LightState fromState, LightState toState) {
        this(direction, fromState, toState, Instant.now());
    }
}
