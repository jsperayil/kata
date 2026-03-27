package com.kata.trafficlight;

public class InvalidTransitionException extends RuntimeException {

    public InvalidTransitionException(LightState from, LightState to) {
        super("Cannot transition from " + from + " to " + to);
    }
}
