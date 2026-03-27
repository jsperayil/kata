package com.kata.trafficlight;

public enum LightState {

    RED,
    YELLOW,
    GREEN;

    public LightState getNext() {
        return switch (this) {
            case RED -> GREEN;
            case GREEN -> YELLOW;
            case YELLOW -> RED;
        };
    }

    public boolean canTransitionTo(LightState target) {
        return getNext() == target;
    }
}
