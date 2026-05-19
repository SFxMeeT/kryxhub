package com.kryxhub.kryxhub.submission.controller;

import com.kryxhub.kryxhub.submission.dto.CreatorSubmissionResponseDto;
import com.kryxhub.kryxhub.submission.dto.ReviewSubmissionRequest;
import com.kryxhub.kryxhub.submission.dto.SubmissionModalDetailsDto;
import com.kryxhub.kryxhub.submission.dto.SubmitVideoRequest;
import com.kryxhub.kryxhub.submission.entity.SubmissionEntity;
import com.kryxhub.kryxhub.submission.enums.SubmissionStatus;
import com.kryxhub.kryxhub.submission.service.SubmissionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kryxhub.kryxhub.campaign.enums.CampaignType;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "4. Submissions", description = "Creator video submissions and funder reviews")
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

    @PostMapping(value = "/{submissionId}/answers/{answerId}/image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadAnswerImage(
            @PathVariable UUID submissionId,
            @PathVariable UUID answerId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String imageUrl = submissionService.uploadAnswerImage(submissionId, answerId, authentication.getName(), file);
            
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Answer image uploaded successfully!",
                    "url", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/ui/my-submissions")
    public ResponseEntity<Page<CreatorSubmissionResponseDto>> getMySubmissions(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) SubmissionStatus status,
            @RequestParam(required = false) UUID campaignId,
            @RequestParam(required = false) CampaignType campaignType,
            @RequestParam(defaultValue = "MOST_RECENT") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

            String creatorEmail = jwt.getSubject();

        return ResponseEntity.ok(submissionService.getCreatorSubmissionsFeed(
                creatorEmail, status, campaignId, campaignType, sortBy, page, size));
    }

    @GetMapping("/ui/{submissionId}/payout-details")
    public ResponseEntity<SubmissionModalDetailsDto> getPayoutDetails(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable java.util.UUID submissionId) {

        String creatorEmail = jwt.getSubject();
        return ResponseEntity.ok(submissionService.getSubmissionModalDetails(submissionId, creatorEmail));
    }
}
