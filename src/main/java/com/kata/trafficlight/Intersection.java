package com.kata.trafficlight;

import java.util.EnumMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class Intersection {

    private final Map<Direction, TrafficLight> lights;

    public Intersection() {
        lights = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            lights.put(direction, new TrafficLight());
        }
    }

    public TrafficLight getLight(Direction direction) {
        return lights.get(direction);
    }

    public void transition(Direction direction, LightState newState) {
        getLight(direction).transitionTo(newState);
    }
}
