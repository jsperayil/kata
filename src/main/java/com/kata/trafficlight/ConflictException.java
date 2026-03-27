package com.kata.trafficlight;

public class ConflictException extends RuntimeException {

    public ConflictException(Direction requested, Direction conflicting) {
        super(requested + " cannot be GREEN while " + conflicting + " is GREEN or YELLOW");
    }
}
