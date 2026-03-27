package com.kata.trafficlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.springframework.stereotype.Component;

@Component
@Getter
public class Intersection {

    private final Map<Direction, TrafficLight> lights;
    private final List<StateChangeEvent> history = new ArrayList<>();

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
        LightState previousState = getLight(direction).getState();
        getLight(direction).transitionTo(newState);
        history.add(new StateChangeEvent(direction, previousState, newState));
    }

    public List<StateChangeEvent> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public List<StateChangeEvent> getHistory(Direction direction) {
        return history.stream()
                .filter(event -> event.direction() == direction)
                .toList();
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
