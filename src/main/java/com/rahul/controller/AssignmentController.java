package com.rahul.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rahul.model.Assignment;
import com.rahul.model.Topic;
import com.rahul.repository.AssignmentRepository;
import com.rahul.repository.NoteRepository;
import com.rahul.repository.TopicRepository;


@RestController
public class AssignmentController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private NoteRepository noteRepository;

    @PostMapping("/save-assignment")
    public ResponseEntity<String> saveAssignment(@RequestBody Assignment assignment) {
        logger.info("Received Assignment: {}", assignment);

        if (assignment.getTopic() == null || !topicRepository.existsById(assignment.getTopic().getTopicId())) {
            return new ResponseEntity<>("Topic not found for the assignment!", HttpStatus.BAD_REQUEST);
        }

        Topic topic = assignment.getTopic();
        assignment.setTopic(topic);
        assignmentRepository.save(assignment);
        return new ResponseEntity<>("Assignment saved successfully!", HttpStatus.CREATED);
    }
    @GetMapping("/get-assignment/{assignmentId}")
public ResponseEntity<?> getAssignment(@PathVariable Long assignmentId) {
    Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
    if (assignment.isPresent()) {
        Assignment existingAssignment = assignment.get();

        // Include topic details in the response
        Map<String, Object> response = new HashMap<>();
        response.put("assignmentId", existingAssignment.getAssignmentId());
        response.put("topicId", existingAssignment.getTopic().getTopicId());
        response.put("topicName", existingAssignment.getTopic().getName());  // Add topicName here
        response.put("qa", existingAssignment.getQa());

        return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Assignment not found");
}

    // Update assignment
//     @PutMapping("/update-assignment/{assignmentId}")
// public ResponseEntity<String> updateAssignment(
//         @PathVariable("assignmentId") Long assignmentId,
//         @RequestBody Map<String, Object> requestData) { // Change to Map for flexibility

//     if (assignmentId == null) {
//         return new ResponseEntity<>("Assignment ID is required!", HttpStatus.BAD_REQUEST);
//     }

//     Long topicId = Long.valueOf(requestData.get("topicId").toString()); // Extract topicId
//     if (topicId == null) {
//         return new ResponseEntity<>("Topic ID is required!", HttpStatus.BAD_REQUEST);
//     }

//     Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
//     if (assignmentOptional.isPresent()) {
//         Assignment assignment = assignmentOptional.get();

//         // Set the topicId in the assignment object
//         Topic topic = new Topic();
//         topic.setTopicId(topicId);
//         assignment.setTopic(topic);

//         assignmentRepository.save(assignment);

//         return new ResponseEntity<>("Assignment updated successfully!", HttpStatus.OK);
//     }

//     return new ResponseEntity<>("Assignment not found!", HttpStatus.NOT_FOUND);
// }
@PutMapping("/update-assignment/{assignmentId}")
public ResponseEntity<String> updateAssignment(
        @PathVariable("assignmentId") Long assignmentId,
        @RequestBody Map<String, Object> requestData) { 

    if (assignmentId == null) {
        return new ResponseEntity<>("Assignment ID is required!", HttpStatus.BAD_REQUEST);
    }

    Long topicId = Long.valueOf(requestData.get("topicId").toString());
    if (topicId == null) {
        return new ResponseEntity<>("Topic ID is required!", HttpStatus.BAD_REQUEST);
    }

    Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
    Optional<Topic> topicOptional = topicRepository.findById(topicId);  // Fetch the existing topic

    if (assignmentOptional.isPresent() && topicOptional.isPresent()) {
        Assignment assignment = assignmentOptional.get();

        // Link the existing topic (avoids detached entity issues)
        assignment.setTopic(topicOptional.get());
       
        assignmentRepository.save(assignment);

        return new ResponseEntity<>("Assignment updated successfully!", HttpStatus.OK);
    }

    return new ResponseEntity<>("Assignment or Topic not found!", HttpStatus.NOT_FOUND);
}

    // Delete assignment
    @DeleteMapping("/delete-assignment/{assignmentId}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long assignmentId) {
        if (assignmentRepository.existsById(assignmentId)) {
            assignmentRepository.deleteById(assignmentId);
            return new ResponseEntity<>("Assignment deleted successfully!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Assignment not found!", HttpStatus.NOT_FOUND);
    }


}
