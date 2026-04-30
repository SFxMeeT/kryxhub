package com.kryxhub.kryxhub.dto;

import java.math.BigDecimal;
import java.util.List;

import com.kryxhub.kryxhub.enums.CampaignType;
import com.kryxhub.kryxhub.enums.CampaignCategory;
import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.enums.LinkLabel;
import com.kryxhub.kryxhub.enums.QuestionType;

public class CreateCampaignRequest {

    private String title;
    private String description;
    private CampaignType type;
    private CampaignCategory category;
    private String thumbnailUrl;
    
    private BigDecimal totalBudget;
    
    private List<PlatformDto> platforms;
    private List<String> rules;
    private List<LinkDto> links;
    private List<FaqDto> faqs;

    private Boolean requiresApplication;
    private Boolean showOnDiscover;
    private List<QuestionDto> questions;

    public CreateCampaignRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CampaignType getType() { return type; }
    public void setType(CampaignType type) { this.type = type; }

    public CampaignCategory getCategory() { return category; }
    public void setCategory(CampaignCategory category) { this.category = category; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public List<PlatformDto> getPlatforms() { return platforms; }
    public void setPlatforms(List<PlatformDto> platforms) { this.platforms = platforms; }

    public List<String> getRules() { return rules; }
    public void setRules(List<String> rules) { this.rules = rules; }

    public List<LinkDto> getLinks() { return links; }
    public void setLinks(List<LinkDto> links) { this.links = links; }

    public List<FaqDto> getFaqs() { return faqs; }
    public void setFaqs(List<FaqDto> faqs) { this.faqs = faqs; }

    public Boolean getRequiresApplication() { return requiresApplication; }
    public void setRequiresApplication(Boolean requiresApplication) { this.requiresApplication = requiresApplication; }

    public Boolean getShowOnDiscover() { return showOnDiscover; }
    public void setShowOnDiscover(Boolean showOnDiscover) { this.showOnDiscover = showOnDiscover; }

    public List<QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }

    // --- Nested Classes ---

    public static class PlatformDto {
        private Platforms platformName;
        private BigDecimal cpmRate;
        private BigDecimal minPayout;
        private BigDecimal maxPayout;

        public PlatformDto() {}

        public Platforms getPlatformName() { return platformName; }
        public void setPlatformName(Platforms platformName) { this.platformName = platformName; }

        public BigDecimal getCpmRate() { return cpmRate; }
        public void setCpmRate(BigDecimal cpmRate) { this.cpmRate = cpmRate; }

        public BigDecimal getMinPayout() { return minPayout; }
        public void setMinPayout(BigDecimal minPayout) { this.minPayout = minPayout; }

        public BigDecimal getMaxPayout() { return maxPayout; }
        public void setMaxPayout(BigDecimal maxPayout) { this.maxPayout = maxPayout; }
    }

    public static class LinkDto {
        private LinkLabel label;
        private String url;

        public LinkDto() {}

        public LinkLabel getLabel() { return label; }
        public void setLabel(LinkLabel label) { this.label = label; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class FaqDto {
        private String question;
        private String answer;

        public FaqDto() {}

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }

    public static class QuestionDto {
        private String questionText;
        private QuestionType questionType;
        private Boolean isRequired;

        public QuestionDto() {}

        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }

        public QuestionType getQuestionType() { return questionType; }
        public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

        public Boolean getIsRequired() { return isRequired; }
        public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    }
}