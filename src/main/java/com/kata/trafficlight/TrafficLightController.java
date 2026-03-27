package com.kata.trafficlight;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/light")
public class TrafficLightController {

    private final TrafficLight trafficLight = new TrafficLight();

    @GetMapping
    public TrafficLight getLight() {
        return trafficLight;
    }

    @PutMapping
    public ResponseEntity<TrafficLight> changeState(@RequestBody StateChangeRequest request) {
        LightState newState = LightState.valueOf(request.state().toUpperCase());
        trafficLight.transitionTo(newState);
        return ResponseEntity.ok(trafficLight);
    }

    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidTransitionException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    public record StateChangeRequest(String state) {}

    public record ErrorResponse(String error) {}
}
