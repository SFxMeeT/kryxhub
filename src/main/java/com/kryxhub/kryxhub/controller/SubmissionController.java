package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.dto.SubmitVideoRequest;
import com.kryxhub.kryxhub.entity.SubmissionEntity;
import com.kryxhub.kryxhub.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.kryxhub.kryxhub.dto.ReviewSubmissionRequest;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<?> submitVideo(@RequestBody SubmitVideoRequest request, Authentication authentication) {
        String creatorEmail = authentication.getName();

        try {
            SubmissionEntity savedSubmission = submissionService.submitVideo(request, creatorEmail);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Video submitted successfully! It is now " + savedSubmission.getStatus(),
                    "submissionId", savedSubmission.getId()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "An unexpected error occurred during submission."
            ));
        }
    }

    @PutMapping("/{submissionId}/review")
    public ResponseEntity<?> reviewSubmission(
            @PathVariable UUID submissionId,
            @RequestBody ReviewSubmissionRequest request,
            Authentication authentication) {
        
        String funderEmail = authentication.getName();

        try {
            SubmissionEntity reviewedSubmission = submissionService.reviewSubmission(submissionId, request, funderEmail);
            
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Submission has been " + reviewedSubmission.getStatus(),
                    "submissionId", reviewedSubmission.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}