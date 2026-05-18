package com.kryxhub.kryxhub.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kryxhub.kryxhub.dto.CreateCampaignRequest;
import com.kryxhub.kryxhub.dto.OverviewFeedDto;
import com.kryxhub.kryxhub.dto.CampaignDiscoveryDto;
import com.kryxhub.kryxhub.dto.AdminCampaignDto;

import com.kryxhub.kryxhub.enums.PrimaryPersona;

import com.kryxhub.kryxhub.entity.CampaignEntity;
import com.kryxhub.kryxhub.entity.CampaignFaqEntity;
import com.kryxhub.kryxhub.entity.CampaignLinkEntity;
import com.kryxhub.kryxhub.entity.CampaignPlatformEntity;
import com.kryxhub.kryxhub.entity.CampaignQuestionEntity;
import com.kryxhub.kryxhub.entity.CampaignRuleEntity;
import com.kryxhub.kryxhub.entity.UserEntity;

import com.kryxhub.kryxhub.repository.CampaignRepository;
import com.kryxhub.kryxhub.repository.UserRepository;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

    public CampaignService(CampaignRepository campaignRepository, UserRepository userRepository, S3StorageService s3StorageService) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.s3StorageService = s3StorageService;
    }

    @Transactional
    public CampaignEntity createDraftCampaign(CreateCampaignRequest request, String funderEmail) {
        
        UserEntity funder = userRepository.findByEmail(funderEmail)
                .orElseThrow(() -> new RuntimeException("Funder not found"));
        
        if (funder.getPrimaryPersona() != PrimaryPersona.FUNDER) {
            funder.setPrimaryPersona(PrimaryPersona.FUNDER);
            userRepository.save(funder);
        }

        CampaignEntity campaign = new CampaignEntity();
        campaign.setFunder(funder);
        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setType(request.getType());
        campaign.setCategory(request.getCategory());
        campaign.setThumbnailUrl(request.getThumbnailUrl());
        campaign.setTotalBudget(request.getTotalBudget());
        campaign.setBudgetRemaining(request.getTotalBudget());
        campaign.setRequiresApplication(request.getRequiresApplication() != null ? request.getRequiresApplication() : false);
        campaign.setShowOnDiscover(request.getShowOnDiscover() != null ? request.getShowOnDiscover() : true);
        
        campaign.setStatus("DRAFT");
        campaign.setCreatedAt(OffsetDateTime.now());
        campaign.setExpiresAt(OffsetDateTime.now().plusDays(30)); 

        if (request.getPlatforms() != null) {
            for (CreateCampaignRequest.PlatformDto pDto : request.getPlatforms()) {
                CampaignPlatformEntity platform = new CampaignPlatformEntity();
                platform.setCampaign(campaign);
                platform.setPlatformName(pDto.getPlatformName());
                platform.setCpmRate(pDto.getCpmRate());
                platform.setMinPayout(pDto.getMinPayout() != null ? pDto.getMinPayout() : BigDecimal.ZERO);
                platform.setMaxPayout(pDto.getMaxPayout());
                platform.setCreatedAt(OffsetDateTime.now());
                campaign.getPlatforms().add(platform);
            }
        }

        if (request.getRules() != null) {
            AtomicInteger ruleOrder = new AtomicInteger(0);
            for (String ruleText : request.getRules()) {
                CampaignRuleEntity rule = new CampaignRuleEntity();
                rule.setCampaign(campaign);
                rule.setRuleText(ruleText);
                rule.setDisplayOrder(ruleOrder.getAndIncrement());
                rule.setCreatedAt(OffsetDateTime.now());
                campaign.getRules().add(rule);
            }
        }

        if (request.getLinks() != null) {
            for (CreateCampaignRequest.LinkDto linkDto : request.getLinks()) {
                CampaignLinkEntity link = new CampaignLinkEntity();
                link.setCampaign(campaign);
                link.setLabel(linkDto.getLabel());
                link.setUrl(linkDto.getUrl());
                link.setCreatedAt(OffsetDateTime.now());
                campaign.getLinks().add(link);
            }
        }

        if (request.getFaqs() != null) {
            AtomicInteger faqOrder = new AtomicInteger(0);
            for (CreateCampaignRequest.FaqDto faqDto : request.getFaqs()) {
                CampaignFaqEntity faq = new CampaignFaqEntity();
                faq.setCampaign(campaign);
                faq.setQuestion(faqDto.getQuestion());
                faq.setAnswer(faqDto.getAnswer());
                faq.setDisplayOrder(faqOrder.getAndIncrement());
                faq.setCreatedAt(OffsetDateTime.now());
                campaign.getFaqs().add(faq);
            }
        }

        if (request.getQuestions() != null) {
            AtomicInteger qOrder = new AtomicInteger(0);
            for (CreateCampaignRequest.QuestionDto qDto : request.getQuestions()) {
                CampaignQuestionEntity question = new CampaignQuestionEntity();
                question.setCampaign(campaign);
                question.setQuestionText(qDto.getQuestionText());
                question.setQuestionType(qDto.getQuestionType());
                question.setIsRequired(qDto.getIsRequired() != null ? qDto.getIsRequired() : true);
                question.setDisplayOrder(qOrder.getAndIncrement());
                question.setCreatedAt(OffsetDateTime.now());
                campaign.getQuestions().add(question);
            }
        }

        return campaignRepository.save(campaign);
    }

    public Page<CampaignDiscoveryDto> getDiscoveryFeed(int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CampaignEntity> activeCampaigns = campaignRepository
                .findByStatusAndBudgetRemainingGreaterThan("ACTIVE", BigDecimal.ZERO, pageable);

        return activeCampaigns.map(campaign -> new CampaignDiscoveryDto(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getFunder().getUsername(),
                campaign.getBudgetRemaining()
        ));
    }

    public Page<AdminCampaignDto> getAllCampaignsForAdmin(int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CampaignEntity> allCampaigns = campaignRepository.findAll(pageable);

        return allCampaigns.map(campaign -> new AdminCampaignDto(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getFunder().getUsername(),
                campaign.getFunder().getEmail(),
                campaign.getStatus(),
                campaign.getTotalBudget(),
                campaign.getBudgetRemaining()
        ));
    }

    public String forceCloseCampaign(java.util.UUID campaignId) {
        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found."));

        if ("FORCE_CLOSED".equals(campaign.getStatus())) {
            return "Campaign is already forcefully closed.";
        }

        campaign.setStatus("FORCE_CLOSED");
 
        campaignRepository.save(campaign);

        return "Campaign successfully force-closed. All future payouts have been halted.";
    }

    public String overrideCampaignStatus(java.util.UUID campaignId, String newStatus) {
        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found."));

        String formattedStatus = newStatus.toUpperCase();
        String oldStatus = campaign.getStatus();

        if ("ACTIVE".equals(formattedStatus) && campaign.getBudgetRemaining().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Cannot activate a campaign that has no budget remaining.");
        }

        campaign.setStatus(formattedStatus); 
        
        campaignRepository.save(campaign);

        return "Campaign successfully updated from " + oldStatus + " to " + formattedStatus + ".";
    }

    @Transactional
    public String updateCampaignThumbnail(java.util.UUID campaignId, String funderEmail, org.springframework.web.multipart.MultipartFile file) {
        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!campaign.getFunder().getEmail().equals(funderEmail)) {
            throw new RuntimeException("Unauthorized: You do not own this campaign.");
        }

        String imageUrl = s3StorageService.uploadFile(file, "campaigns");

        campaign.setThumbnailUrl(imageUrl);
        campaignRepository.save(campaign);

        return imageUrl;
    }

    private String calculateTimeAgo(OffsetDateTime createdAt) {
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(createdAt, OffsetDateTime.now());
        if (minutes < 60) return minutes + " minutes ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        return (hours / 24) + " days ago";
    }

    @Transactional(readOnly = true)
    public Page<OverviewFeedDto> getOverviewFeed(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CampaignEntity> campaigns = campaignRepository.findByStatusAndBudgetRemainingGreaterThan(
                "ACTIVE", BigDecimal.ZERO, pageable);

        return campaigns.map(c -> {
            java.util.List<String> platformNames = c.getPlatforms().stream()
                    .map(p -> p.getPlatformName().name())
                    .toList();

            String cpmDisplay = c.getPlatforms().isEmpty() ? "N/A" : 
                    "$" + c.getPlatforms().get(0).getCpmRate() + " per 1,000 Views";

            String budgetDisplay = "$" + String.format("%,.0f", c.getTotalBudget());

            OverviewFeedDto.FeedStats stats = new OverviewFeedDto.FeedStats(
                    c.getLikes().size(),
                    c.getComments().size(),
                    c.getViewCount() != null ? c.getViewCount() : 0
            );

            return new OverviewFeedDto(
                    c.getId(),
                    c.getFunder().getProfilePicUrl(),
                    "@" + c.getFunder().getUsername(),
                    c.getType().name(),
                    calculateTimeAgo(c.getCreatedAt()),
                    c.getTitle(),
                    cpmDisplay,
                    budgetDisplay,
                    platformNames,
                    c.getThumbnailUrl(),
                    stats
            );
        });
    }
}