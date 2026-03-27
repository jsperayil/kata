package com.kata.trafficlight;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntersectionControllerTest {

    private IntersectionController controller;

    @BeforeEach
    void setUp() {
        controller = new IntersectionController(new Intersection());
    }

    @Nested
    class GetIntersection {

        @Test
        void shouldReturnBothDirections() {
            Map<Direction, TrafficLight> lights = controller.getIntersection();

            assertEquals(2, lights.size());
            assertEquals(LightState.RED, lights.get(Direction.NORTH_SOUTH).getState());
            assertEquals(LightState.RED, lights.get(Direction.EAST_WEST).getState());
        }
    }

    @Nested
    class ValidTransitions {

        @Test
        void shouldTransitionNorthSouthToGreen() {
            var response = controller.changeState(Direction.NORTH_SOUTH, request("GREEN"));

            assertEquals(LightState.GREEN, response.getBody().getState());
        }

        @Test
        void shouldNotAffectOtherDirection() {
            controller.changeState(Direction.NORTH_SOUTH, request("GREEN"));

            Map<Direction, TrafficLight> lights = controller.getIntersection();

            assertEquals(LightState.GREEN, lights.get(Direction.NORTH_SOUTH).getState());
            assertEquals(LightState.RED, lights.get(Direction.EAST_WEST).getState());
        }

        @Test
        void shouldFollowFullCyclePerDirection() {
            controller.changeState(Direction.EAST_WEST, request("GREEN"));
            controller.changeState(Direction.EAST_WEST, request("YELLOW"));
            var response = controller.changeState(Direction.EAST_WEST, request("RED"));

            assertEquals(LightState.RED, response.getBody().getState());
        }

        @Test
        void shouldAllowGreenAfterOtherDirectionCompletedCycle() {
            // NORTH_SOUTH goes through full cycle back to RED
            controller.changeState(Direction.NORTH_SOUTH, request("GREEN"));
            controller.changeState(Direction.NORTH_SOUTH, request("YELLOW"));
            controller.changeState(Direction.NORTH_SOUTH, request("RED"));

            // Now EAST_WEST can go GREEN
            var response = controller.changeState(Direction.EAST_WEST, request("GREEN"));

            assertEquals(LightState.GREEN, response.getBody().getState());
        }
    }

    @Nested
    class ConflictSafety {

        @Test
        void shouldRejectGreenWhenOtherDirectionIsGreen() {
            controller.changeState(Direction.NORTH_SOUTH, request("GREEN"));

            assertThrows(ConflictException.class,
                    () -> controller.changeState(Direction.EAST_WEST, request("GREEN")));
        }

        @Test
        void shouldRejectGreenWhenOtherDirectionIsYellow() {
            controller.changeState(Direction.NORTH_SOUTH, request("GREEN"));
            controller.changeState(Direction.NORTH_SOUTH, request("YELLOW"));

            assertThrows(ConflictException.class,
                    () -> controller.changeState(Direction.EAST_WEST, request("GREEN")));
        }

        @Test
        void shouldPreserveStateAfterRejectedConflict() {
            controller.changeState(Direction.NORTH_SOUTH, request("GREEN"));

            assertThrows(ConflictException.class,
                    () -> controller.changeState(Direction.EAST_WEST, request("GREEN")));

            // Both directions should be unchanged
            assertEquals(LightState.GREEN, controller.getIntersection().get(Direction.NORTH_SOUTH).getState());
            assertEquals(LightState.RED, controller.getIntersection().get(Direction.EAST_WEST).getState());
        }
    }

    @Nested
    class InvalidTransitions {

        @Test
        void shouldRejectInvalidTransition() {
            assertThrows(InvalidTransitionException.class,
                    () -> controller.changeState(Direction.NORTH_SOUTH, request("YELLOW")));
        }
    }

    private IntersectionController.StateChangeRequest request(String state) {
        return new IntersectionController.StateChangeRequest(state);
    }
}
