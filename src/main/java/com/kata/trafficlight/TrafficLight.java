package com.kata.trafficlight;

public class TrafficLight {

    private LightState state;

    public TrafficLight() {
        this.state = LightState.RED;
    }

    public LightState getState() {
        return state;
    }

    public void setState(LightState state) {
        this.state = state;
    }
}
