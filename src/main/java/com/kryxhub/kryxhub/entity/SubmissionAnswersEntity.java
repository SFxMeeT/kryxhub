package com.kryxhub.kryxhub.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "submission_answers", indexes = {
        @Index(name = "idx_answers_submission_id", columnList = "submission_id")
})
public class SubmissionAnswersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubmissionsEntity submission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CampaignQuestionsEntity question;

    @Column(name = "answer_value", nullable = false, columnDefinition = "TEXT")
    private String answerValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public SubmissionAnswersEntity() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SubmissionsEntity getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionsEntity submission) {
        this.submission = submission;
    }

    public CampaignQuestionsEntity getQuestion() {
        return question;
    }

    public void setQuestion(CampaignQuestionsEntity question) {
        this.question = question;
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
