package com.kryxhub.kryxhub.dto;

public class ReviewSubmissionRequest {
    private Boolean isApproved;
    private String funderNotes;

    public ReviewSubmissionRequest() {}

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public String getFunderNotes() { return funderNotes; }
    public void setFunderNotes(String funderNotes) { this.funderNotes = funderNotes; }
}