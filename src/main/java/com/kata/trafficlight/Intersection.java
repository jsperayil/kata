package com.kata.trafficlight;

import java.util.EnumMap;
import java.util.Map;

import lombok.Getter;

import org.springframework.stereotype.Component;

@Component
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
        if (newState == LightState.GREEN) {
            checkForConflicts(direction);
        }
        getLight(direction).transitionTo(newState);
    }

    private void checkForConflicts(Direction requested) {
        for (Direction other : Direction.values()) {
            if (other == requested) {
                continue;
            }
            LightState otherState = getLight(other).getState();
            if (otherState == LightState.GREEN || otherState == LightState.YELLOW) {
                throw new ConflictException(requested, other);
            }
        }
    }
}
