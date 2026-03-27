package com.kata.trafficlight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrafficCycleServiceTest {

    private Intersection intersection;
    private TrafficCycleService cycleService;

    @BeforeEach
    void setUp() {
        intersection = new Intersection();
        cycleService = new TrafficCycleService(intersection);
    }

    @Nested
    class SingleDirection {

        @Test
        void shouldStartByTurningFirstDirectionGreen() {
            cycleService.advanceCycle();

            assertEquals(LightState.GREEN, lightState(Direction.NORTH_SOUTH));
            assertEquals(LightState.RED, lightState(Direction.EAST_WEST));
        }

        @Test
        void shouldTransitionFromGreenToYellow() {
            cycleService.advanceCycle(); // RED → GREEN
            cycleService.advanceCycle(); // GREEN → YELLOW

            assertEquals(LightState.YELLOW, lightState(Direction.NORTH_SOUTH));
        }

        @Test
        void shouldTransitionFromYellowToRed() {
            cycleService.advanceCycle(); // RED → GREEN
            cycleService.advanceCycle(); // GREEN → YELLOW
            cycleService.advanceCycle(); // YELLOW → RED (switches direction)

            assertEquals(LightState.RED, lightState(Direction.NORTH_SOUTH));
        }
    }

    @Nested
    class DirectionSwitching {

        @Test
        void shouldSwitchToEastWestAfterNorthSouthCompletes() {
            advanceTimes(3); // NORTH_SOUTH: RED→GREEN→YELLOW→RED
            cycleService.advanceCycle(); // EAST_WEST: RED→GREEN

            assertEquals(LightState.RED, lightState(Direction.NORTH_SOUTH));
            assertEquals(LightState.GREEN, lightState(Direction.EAST_WEST));
        }

        @Test
        void shouldCompleteFullCycleBackToNorthSouth() {
            advanceTimes(3); // NORTH_SOUTH: full cycle
            advanceTimes(3); // EAST_WEST: full cycle
            cycleService.advanceCycle(); // NORTH_SOUTH: RED→GREEN again

            assertEquals(LightState.GREEN, lightState(Direction.NORTH_SOUTH));
            assertEquals(LightState.RED, lightState(Direction.EAST_WEST));
        }
    }

    @Nested
    class StoppedBehavior {

        @Test
        void shouldNotAdvanceWhenNotRunning() {
            cycleService.shutdown();
            cycleService.advanceCycle();

            assertEquals(LightState.RED, lightState(Direction.NORTH_SOUTH));
            assertEquals(LightState.RED, lightState(Direction.EAST_WEST));
        }
    }

    private LightState lightState(Direction direction) {
        return intersection.getLight(direction).getState();
    }

    private void advanceTimes(int times) {
        for (int i = 0; i < times; i++) {
            cycleService.advanceCycle();
        }
    }
}
