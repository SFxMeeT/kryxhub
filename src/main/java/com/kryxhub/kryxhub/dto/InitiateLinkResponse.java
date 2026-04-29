package com.kryxhub.kryxhub.dto;

import java.util.UUID;

public class InitiateLinkResponse {

    private UUID accountId;
    private String verificationCode;

    public InitiateLinkResponse(UUID accountId, String verificationCode) {
        this.accountId = accountId;
        this.verificationCode = verificationCode;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
