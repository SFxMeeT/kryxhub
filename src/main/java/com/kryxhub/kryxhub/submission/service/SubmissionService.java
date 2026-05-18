package com.kryxhub.kryxhub.submission.service;

import com.kryxhub.kryxhub.integration.service.ExternalApiRouter;
import com.kryxhub.kryxhub.submission.dto.CreatorSubmissionResponseDto;
import com.kryxhub.kryxhub.submission.dto.ReviewSubmissionRequest;
import com.kryxhub.kryxhub.submission.dto.SubmissionModalDetailsDto;
import com.kryxhub.kryxhub.submission.dto.SubmitVideoRequest;
import com.kryxhub.kryxhub.submission.dto.VideoStatsDto;
import com.kryxhub.kryxhub.submission.entity.SubmissionAnswerEntity;
import com.kryxhub.kryxhub.submission.entity.SubmissionEntity;
import com.kryxhub.kryxhub.submission.enums.SubmissionStatus;
import com.kryxhub.kryxhub.submission.repository.SubmissionAnswerRepository;
import com.kryxhub.kryxhub.submission.repository.SubmissionRepository;
import com.kryxhub.kryxhub.user.entity.UserEntity;
import com.kryxhub.kryxhub.user.enums.PrimaryPersona;
import com.kryxhub.kryxhub.user.repository.LinkedSocialAccountRepository;
import com.kryxhub.kryxhub.user.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kryxhub.kryxhub.campaign.entity.CampaignEntity;
import com.kryxhub.kryxhub.campaign.entity.CampaignQuestionEntity;
import com.kryxhub.kryxhub.campaign.enums.CampaignType;
import com.kryxhub.kryxhub.campaign.repository.CampaignQuestionRepository;
import com.kryxhub.kryxhub.campaign.repository.CampaignRepository;
import com.kryxhub.kryxhub.core.service.S3StorageService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
    private final S3StorageService s3StorageService;
    private final SubmissionAnswerRepository answerRepository;

    public SubmissionService(SubmissionRepository submissionRepository, 
                             CampaignRepository campaignRepository, 
                             UserRepository userRepository, 
                             LinkedSocialAccountRepository linkedAccountRepository,
                             CampaignQuestionRepository questionRepository,
                             ExternalApiRouter apiRouter,
                             S3StorageService s3StorageService,
                             SubmissionAnswerRepository answerRepository) {
        this.submissionRepository = submissionRepository;
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.linkedAccountRepository = linkedAccountRepository;
        this.questionRepository = questionRepository;
        this.apiRouter = apiRouter;
        this.s3StorageService = s3StorageService;
        this.answerRepository = answerRepository;
    }

    @Transactional
    public SubmissionEntity submitVideo(SubmitVideoRequest request, String creatorEmail) {

        UserEntity creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));
        
        if (creator.getPrimaryPersona() != PrimaryPersona.CREATOR) {
            creator.setPrimaryPersona(PrimaryPersona.CREATOR);
            userRepository.save(creator);
        }

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

    @Transactional
    public String uploadAnswerImage(java.util.UUID submissionId, java.util.UUID answerId, String creatorEmail, org.springframework.web.multipart.MultipartFile file) {
        
        SubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (!submission.getCreator().getEmail().equals(creatorEmail)) {
            throw new RuntimeException("Unauthorized: You do not own this submission.");
        }

        SubmissionAnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        if (!answer.getSubmission().getId().equals(submissionId)) {
            throw new RuntimeException("Security Error: Answer does not belong to this submission.");
        }

        String imageUrl = s3StorageService.uploadFile(file, "submission-proofs");

        answer.setAnswerValue(imageUrl);
        answerRepository.save(answer);

        return imageUrl;
    }

    @Transactional(readOnly = true)
    public Page<CreatorSubmissionResponseDto> getCreatorSubmissionsFeed(
            String creatorEmail, SubmissionStatus status, UUID campaignId,
            CampaignType campaignType, String sortBy, int page, int size) {
        
            Sort sortOrder = switch (sortBy != null ? sortBy.toUpperCase() : "MOST_RECENT") {
            case "MOST_VIEWED" -> Sort.by(Sort.Direction.DESC, "currentViews");
            case "MOST_EARNED" -> Sort.by(Sort.Direction.DESC, "totalEarned");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        
        Page<SubmissionEntity> submissions = submissionRepository
                .findCreatorSubmissionsWithFilters(creatorEmail, status, campaignId, campaignType, pageable);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

        return submissions.map(s -> {

            int minViews = s.getCampaign().getPlatforms().stream()
                    .filter(p -> p.getPlatformName().name().equals(s.getPlatformName()))
                    .findFirst()
                    .map(p -> {
                        if (p.getCpmRate().compareTo(BigDecimal.ZERO) == 0) return 0;
                        return p.getMinPayout().divide(p.getCpmRate(), RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(1000)).intValue();
                    }).orElse(0);

            String formattedDate = s.getReviewedAt() != null ? s.getReviewedAt().format(dateFormatter) : "Recent";

            return new CreatorSubmissionResponseDto(
                    s.getId(), s.getVideoTitle(), s.getVideoUrl(), s.getCampaign().getTitle(),
                    "@" + s.getCreator().getUsername(), s.getStatus().name(), s.getCurrentViews(),
                    minViews, formattedDate, "$" + s.getEstimatedPayout()
            );
        });
    }

    @Transactional(readOnly = true)
    public SubmissionModalDetailsDto getSubmissionModalDetails(UUID submissionId, String creatorEmail) {
        SubmissionEntity s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission record not found"));

        if (!s.getCreator().getEmail().equals(creatorEmail)) {
            throw new RuntimeException("Unauthorized data access exception");
        }

        var platformOpt = s.getCampaign().getPlatforms().stream()
                .filter(p -> p.getPlatformName().name().equals(s.getPlatformName())).findFirst();

        int minViews = platformOpt.map(p -> {
            if (p.getCpmRate().compareTo(BigDecimal.ZERO) == 0) return 0;
            return p.getMinPayout().divide(p.getCpmRate(), RoundingMode.HALF_UP)
                    .multiply(new java.math.BigDecimal(1000)).intValue();
        }).orElse(0);

        String minThresholdDisplay = platformOpt.map(p -> "$" + p.getMinPayout()).orElse("$0.00");

        String notesDisplay = s.getFunderNotes() != null ? s.getFunderNotes() : "No notes yet";

        return new SubmissionModalDetailsDto(
                s.getVideoTitle(), s.getCampaign().getTitle(), s.getStatus().name(),
                s.getCurrentViews(), minViews, "Recent", notesDisplay,
                "$" + s.getTotalEarned(), "$" + s.getTotalEarned(), "-", "-", minThresholdDisplay
        );
    }
}