package com.kata.trafficlight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrafficLightControllerTest {

    private TrafficLightController controller;

    @BeforeEach
    void setUp() {
        controller = new TrafficLightController();
    }

    @Test
    void shouldReturnRedAsDefaultState() {
        TrafficLight light = controller.getLight();

        assertEquals(LightState.RED, light.getState());
    }

    @Nested
    class ValidTransitions {

        @Test
        void shouldTransitionFromRedToGreen() {
            var response = controller.changeState(request("GREEN"));

            assertEquals(LightState.GREEN, response.getBody().getState());
        }

        @Test
        void shouldTransitionFromGreenToYellow() {
            controller.changeState(request("GREEN"));

            var response = controller.changeState(request("YELLOW"));

            assertEquals(LightState.YELLOW, response.getBody().getState());
        }

        @Test
        void shouldTransitionFromYellowToRed() {
            controller.changeState(request("GREEN"));
            controller.changeState(request("YELLOW"));

            var response = controller.changeState(request("RED"));

            assertEquals(LightState.RED, response.getBody().getState());
        }

        @Test
        void shouldCompleteFullCycle() {
            controller.changeState(request("GREEN"));
            controller.changeState(request("YELLOW"));
            controller.changeState(request("RED"));
            var response = controller.changeState(request("GREEN"));

            assertEquals(LightState.GREEN, response.getBody().getState());
        }
    }

    @Nested
    class InvalidTransitions {

        @Test
        void shouldRejectRedToYellow() {
            assertThrows(InvalidTransitionException.class,
                    () -> controller.changeState(request("YELLOW")));
        }

        @Test
        void shouldRejectRedToRed() {
            assertThrows(InvalidTransitionException.class,
                    () -> controller.changeState(request("RED")));
        }

        @Test
        void shouldRejectGreenToRed() {
            controller.changeState(request("GREEN"));

            assertThrows(InvalidTransitionException.class,
                    () -> controller.changeState(request("RED")));
        }

        @Test
        void shouldRejectYellowToGreen() {
            controller.changeState(request("GREEN"));
            controller.changeState(request("YELLOW"));

            assertThrows(InvalidTransitionException.class,
                    () -> controller.changeState(request("GREEN")));
        }
    }

    @Test
    void shouldHandleLowercaseInput() {
        var response = controller.changeState(request("green"));

        assertEquals(LightState.GREEN, response.getBody().getState());
    }

    private TrafficLightController.StateChangeRequest request(String state) {
        return new TrafficLightController.StateChangeRequest(state);
    }
}
