package com.kryxhub.kryxhub.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kryxhub.kryxhub.dto.CreateCampaignRequest;

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

    public CampaignService(CampaignRepository campaignRepository, UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
    }

    @Transactional // IMPORTANT: If any part fails, the whole save rolls back!
    public CampaignEntity createDraftCampaign(CreateCampaignRequest request, String funderEmail) {
        
        // 1. Get the Brand/Funder
        UserEntity funder = userRepository.findByEmail(funderEmail)
                .orElseThrow(() -> new RuntimeException("Funder not found"));

        // 2. Build the main Campaign Entity
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

        // 3. Map Platforms
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

        // 4. Map Rules (Requirements)
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

        // 5. Map Content Links (COMPLETED)
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

        // 6. Map FAQs (COMPLETED)
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

        // 7. Map Questions (COMPLETED)
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

        // 8. Save the Campaign (Cascades to all children)
        return campaignRepository.save(campaign);
    }
}