package com.kryxhub.kryxhub.dto;

import com.kryxhub.kryxhub.enums.Platforms;
import java.util.List;
import java.util.UUID;

public class SubmitVideoRequest {

    private UUID campaignId;
    private String videoUrl;
    private Platforms platformName;
    private List<AnswerDto> answers;

    public SubmitVideoRequest() {
    }

    public UUID getCampaignId() { return campaignId; }
    public void setCampaignId(UUID campaignId) { this.campaignId = campaignId; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public Platforms getPlatformName() { return platformName; }
    public void setPlatformName(Platforms platformName) { this.platformName = platformName; }

    public List<AnswerDto> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDto> answers) { this.answers = answers; }

    public static class AnswerDto {
        private UUID questionId;
        private String answerValue;

        public AnswerDto() {}

        public UUID getQuestionId() { return questionId; }
        public void setQuestionId(UUID questionId) { this.questionId = questionId; }

        public String getAnswerValue() { return answerValue; }
        public void setAnswerValue(String answerValue) { this.answerValue = answerValue; }
    }
}