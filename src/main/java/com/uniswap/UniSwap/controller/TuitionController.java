// src/main/java/com/uniswap/UniSwap/controller/TuitionController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.Tuition;
import com.uniswap.UniSwap.service.TuitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tuitions")
public class TuitionController {

    @Autowired
    private TuitionService tuitionService;

    @GetMapping
    public ResponseEntity<List<Tuition>> getAllTuitions() {
        List<Tuition> tuitions = tuitionService.getAllTuitions();
        return ResponseEntity.ok(tuitions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tuition> getTuitionById(@PathVariable Integer id) {
        Optional<Tuition> tuition = tuitionService.getTuitionById(id);
        return tuition.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tuition>> getTuitionsByUser(@PathVariable Integer userId) {
        List<Tuition> tuitions = tuitionService.getTuitionsByUser(userId);
        return ResponseEntity.ok(tuitions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Tuition>> getTuitionsByStatus(@PathVariable String status) {
        List<Tuition> tuitions = tuitionService.getTuitionsByStatus(status);
        return ResponseEntity.ok(tuitions);
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Tuition>> getTuitionsBySubject(@PathVariable String subject) {
        List<Tuition> tuitions = tuitionService.getTuitionsBySubject(subject);
        return ResponseEntity.ok(tuitions);
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Tuition>> getTuitionsByLocation(@PathVariable Integer locationId) {
        List<Tuition> tuitions = tuitionService.getTuitionsByLocation(locationId);
        return ResponseEntity.ok(tuitions);
    }

    @GetMapping("/salary/max/{maxSalary}")
    public ResponseEntity<List<Tuition>> getTuitionsBySalaryRange(@PathVariable Integer maxSalary) {
        List<Tuition> tuitions = tuitionService.getTuitionsBySalaryRange(maxSalary);
        return ResponseEntity.ok(tuitions);
    }

    @PostMapping
    public ResponseEntity<Tuition> createTuition(@RequestBody Tuition tuition, @RequestParam Integer userId) {
        try {
            Tuition createdTuition = tuitionService.createTuition(tuition, userId);
            return ResponseEntity.ok(createdTuition);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tuition> updateTuition(@PathVariable Integer id, @RequestBody Tuition tuition) {
        try {
            Tuition updatedTuition = tuitionService.updateTuition(id, tuition);
            return ResponseEntity.ok(updatedTuition);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTuition(@PathVariable Integer id) {
        try {
            tuitionService.deleteTuition(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/take")
    public ResponseEntity<Tuition> markAsTaken(@PathVariable Integer id) {
        try {
            Tuition tuition = tuitionService.markAsTaken(id);
            return ResponseEntity.ok(tuition);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Tuition> markAsCompleted(@PathVariable Integer id) {
        try {
            Tuition tuition = tuitionService.markAsCompleted(id);
            return ResponseEntity.ok(tuition);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
