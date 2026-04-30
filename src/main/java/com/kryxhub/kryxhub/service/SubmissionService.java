package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.SubmitVideoRequest;
import com.kryxhub.kryxhub.entity.*;
import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.enums.SubmissionStatus;
import com.kryxhub.kryxhub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final LinkedSocialAccountRepository linkedAccountRepository;
    private final CampaignQuestionRepository questionRepository;

    public SubmissionService(SubmissionRepository submissionRepository, 
                             CampaignRepository campaignRepository, 
                             UserRepository userRepository, 
                             LinkedSocialAccountRepository linkedAccountRepository,
                             CampaignQuestionRepository questionRepository) {
        this.submissionRepository = submissionRepository;
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.linkedAccountRepository = linkedAccountRepository;
        this.questionRepository = questionRepository;
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

        OffsetDateTime simulatedUploadTime = OffsetDateTime.now().minusMinutes(5); 
        
        long minutesSinceUpload = ChronoUnit.MINUTES.between(simulatedUploadTime, OffsetDateTime.now());
        if (minutesSinceUpload > 30) {
            throw new RuntimeException("Video is too old! Must be submitted within 30 minutes of uploading.");
        }

        SubmissionEntity submission = new SubmissionEntity();
        submission.setCampaign(campaign);
        submission.setCreator(creator);
        submission.setVideoUrl(request.getVideoUrl());
        submission.setPlatformName(request.getPlatformName());

        submission.setCurrentViews(0);
        submission.setTotalEarned(BigDecimal.ZERO);
        submission.setEstimatedPayout(BigDecimal.ZERO);
        
        submission.setStatus(campaign.getRequiresApplication() ? SubmissionStatus.PENDING : SubmissionStatus.APPROVED);

        submission.setVideoTitle("My Awesome Submission"); 

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
}