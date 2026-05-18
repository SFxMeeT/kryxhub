package com.kryxhub.kryxhub.user.service;

import com.kryxhub.kryxhub.user.dto.LinkedAccountsResponseDto;
import com.kryxhub.kryxhub.user.entity.LinkedSocialAccountEntity;
import com.kryxhub.kryxhub.user.repository.LinkedSocialAccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LinkedAccountService {

    private final LinkedSocialAccountRepository linkedAccountRepository;

    public LinkedAccountService(LinkedSocialAccountRepository linkedAccountRepository) {
        this.linkedAccountRepository = linkedAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<LinkedAccountsResponseDto> getGroupedLinkedAccounts(String userEmail) {

        List<LinkedSocialAccountEntity> allAccounts = linkedAccountRepository.findByUserEmail(userEmail);

        Map<String, List<LinkedSocialAccountEntity>> groupedAccounts = allAccounts.stream()
                .collect(Collectors.groupingBy(acc -> acc.getPlatform().name()));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        return groupedAccounts.entrySet().stream().map(entry -> {
            
            String platformName = entry.getKey();
            List<LinkedSocialAccountEntity> accountsForPlatform = entry.getValue();

            List<LinkedAccountsResponseDto.AccountDetail> accountDetails = accountsForPlatform.stream().map(acc -> {

                String displayUsername = acc.getPlatformUsername().startsWith("@") ? 
                        acc.getPlatformUsername() : "@" + acc.getPlatformUsername();

                String verifiedDisplay = acc.getVerified() ? 
                        "Verified " + acc.getCreatedAt().format(dateFormatter) : "Unverified";

                return new LinkedAccountsResponseDto.AccountDetail(
                        acc.getId(), 
                        displayUsername, 
                        verifiedDisplay
                );
            }).toList();

            return new LinkedAccountsResponseDto(platformName, accountDetails.size(), accountDetails);
            
        }).toList();
    }

    @Transactional
    public String unlinkAccount(UUID accountId, String userEmail) {
        
        com.kryxhub.kryxhub.user.entity.LinkedSocialAccountEntity account = linkedAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized: You cannot unlink an account you do not own.");
        }

        linkedAccountRepository.delete(account);
        
        return "Account successfully unlinked.";
    }
}