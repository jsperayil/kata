package com.kata.trafficlight;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;

@Service
public class TrafficCycleService {

    private final Intersection intersection;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Getter
    private boolean running = true;

    @Value("${traffic.green.duration:10}")
    private long greenDurationSeconds;

    @Value("${traffic.yellow.duration:3}")
    private long yellowDurationSeconds;

    private int currentDirectionIndex;

    public TrafficCycleService(Intersection intersection) {
        this.intersection = intersection;
    }

    @PostConstruct
    public void start() {
        running = true;
        scheduleNextPhase(0);
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        scheduler.shutdownNow();
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        if (running) {
            return;
        }
        running = true;
        scheduleNextPhase(0);
    }

    void scheduleNextPhase(long delaySeconds) {
        if (!running) {
            return;
        }
        scheduler.schedule(this::advanceCycle, delaySeconds, TimeUnit.SECONDS);
    }

    void advanceCycle() {
        Direction current = Direction.values()[currentDirectionIndex];
        LightState state = intersection.getLight(current).getState();

        switch (state) {
            case RED -> {
                intersection.transition(current, LightState.GREEN);
                scheduleNextPhase(greenDurationSeconds);
            }
            case GREEN -> {
                intersection.transition(current, LightState.YELLOW);
                scheduleNextPhase(yellowDurationSeconds);
            }
            case YELLOW -> {
                intersection.transition(current, LightState.RED);
                currentDirectionIndex = (currentDirectionIndex + 1) % Direction.values().length;
                scheduleNextPhase(0);
            }
        }
    }
}
