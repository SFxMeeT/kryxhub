package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.SubmitVideoRequest;
import com.kryxhub.kryxhub.dto.VideoStatsDto;
import com.kryxhub.kryxhub.entity.*;
import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import com.kryxhub.kryxhub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kryxhub.kryxhub.dto.ReviewSubmissionRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final LinkedSocialAccountRepository linkedAccountRepository;
    private final CampaignQuestionRepository questionRepository;
    private final ExternalApiRouter apiRouter;

    public SubmissionService(SubmissionRepository submissionRepository, 
                             CampaignRepository campaignRepository, 
                             UserRepository userRepository, 
                             LinkedSocialAccountRepository linkedAccountRepository,
                             CampaignQuestionRepository questionRepository,
                             ExternalApiRouter apiRouter) {
        this.submissionRepository = submissionRepository;
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.linkedAccountRepository = linkedAccountRepository;
        this.questionRepository = questionRepository;
        this.apiRouter = apiRouter;
    }

    @Transactional
    public SubmissionEntity submitVideo(SubmitVideoRequest request, String creatorEmail) {

        UserEntity creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        CampaignEntity campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        boolean hasVerifiedAccount = linkedAccountRepository.existsByUserAndPlatformAndIsVerified(
                creator, request.getPlatformName(), true);
        
        if (!hasVerifiedAccount) {
            throw new RuntimeException("You must link and verify a " + request.getPlatformName() + " account before submitting.");
        }

        if (submissionRepository.existsByVideoUrl(request.getVideoUrl())) {
            throw new RuntimeException("This video has already been submitted to KryxHub.");
        }

        VideoStatsDto videoStats = apiRouter.getStats(request.getPlatformName(), request.getVideoUrl());

        long minutesSinceUpload = ChronoUnit.MINUTES.between(videoStats.getUploadedAt(), OffsetDateTime.now());
        if (minutesSinceUpload > 30) {
            throw new RuntimeException("Video is too old! Must be submitted within 30 minutes of uploading. (Uploaded " + minutesSinceUpload + " mins ago).");
        }

        SubmissionEntity submission = new SubmissionEntity();
        submission.setCampaign(campaign);
        submission.setCreator(creator);
        submission.setVideoUrl(request.getVideoUrl());
        submission.setPlatformName(request.getPlatformName());

        submission.setVideoTitle(videoStats.getVideoTitle()); 
        
        submission.setCurrentViews(0);
        submission.setTotalEarned(BigDecimal.ZERO);
        submission.setEstimatedPayout(BigDecimal.ZERO);
        
        submission.setStatus(campaign.getRequiresApplication() ? SubmissionStatus.PENDING : SubmissionStatus.APPROVED);

        if (request.getAnswers() != null) {
            for (SubmitVideoRequest.AnswerDto answerDto : request.getAnswers()) {
                CampaignQuestionEntity question = questionRepository.findById(answerDto.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Invalid question ID"));

                SubmissionAnswerEntity answerEntity = new SubmissionAnswerEntity();
                answerEntity.setSubmission(submission);
                answerEntity.setQuestion(question);
                answerEntity.setAnswerValue(answerDto.getAnswerValue());
                answerEntity.setCreatedAt(OffsetDateTime.now());
                
                submission.getAnswers().add(answerEntity);
            }
        }

        return submissionRepository.save(submission);
    }

    @Transactional
    public SubmissionEntity reviewSubmission(UUID submissionId, ReviewSubmissionRequest request, String funderEmail) {
        

        SubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        String actualFunderEmail = submission.getCampaign().getFunder().getEmail();
        if (!actualFunderEmail.equals(funderEmail)) {
            throw new RuntimeException("UNAUTHORIZED: You do not have permission to review submissions for this campaign.");
        }

        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new RuntimeException("This submission has already been " + submission.getStatus());
        }

        if (request.getIsApproved()) {
            submission.setStatus(SubmissionStatus.APPROVED);
        } else {
            submission.setStatus(SubmissionStatus.REJECTED);
        }

        submission.setFunderNotes(request.getFunderNotes());
        submission.setReviewedAt(OffsetDateTime.now());

        return submissionRepository.save(submission);
    }
}