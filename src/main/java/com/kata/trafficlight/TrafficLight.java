package com.kata.trafficlight;

import lombok.Getter;

@Getter
public class TrafficLight {

    private LightState state = LightState.RED;

    public void transitionTo(LightState newState) {
        if (!state.canTransitionTo(newState)) {
            throw new InvalidTransitionException(state, newState);
        }
        this.state = newState;
    }
}
