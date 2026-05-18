package com.kryxhub.kryxhub.user.dto;

import java.util.List;
import java.util.UUID;

public class LinkedAccountsResponseDto {

    private String platformName;
    private int accountCount;
    private List<AccountDetail> accounts;

    public LinkedAccountsResponseDto(String platformName, int accountCount, List<AccountDetail> accounts) {
        this.platformName = platformName;
        this.accountCount = accountCount;
        this.accounts = accounts;
    }

    public static class AccountDetail {
        private UUID accountId;
        private String username;
        private String verifiedStatusDisplay;

        public AccountDetail(UUID accountId, String username, String verifiedStatusDisplay) {
            this.accountId = accountId;
            this.username = username;
            this.verifiedStatusDisplay = verifiedStatusDisplay;
        }

        public UUID getAccountId() { return accountId; }
        public String getUsername() { return username; }
        public String getVerifiedStatusDisplay() { return verifiedStatusDisplay; }
    }

    public String getPlatformName() { return platformName; }
    public int getAccountCount() { return accountCount; }
    public List<AccountDetail> getAccounts() { return accounts; }
}