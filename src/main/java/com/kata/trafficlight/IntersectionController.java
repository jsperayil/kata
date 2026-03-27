package com.kata.trafficlight;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/intersection")
public class IntersectionController {

    private final Intersection intersection = new Intersection();

    @GetMapping
    public Map<Direction, TrafficLight> getIntersection() {
        return intersection.getLights();
    }

    @PutMapping("/{direction}")
    public ResponseEntity<TrafficLight> changeState(
            @PathVariable Direction direction,
            @RequestBody StateChangeRequest request) {
        LightState newState = LightState.valueOf(request.state().toUpperCase());
        intersection.transition(direction, newState);
        return ResponseEntity.ok(intersection.getLight(direction));
    }

    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidTransitionException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    public record StateChangeRequest(String state) {}

    public record ErrorResponse(String error) {}
}
