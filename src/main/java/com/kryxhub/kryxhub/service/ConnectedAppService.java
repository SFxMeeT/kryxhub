package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.dto.InitiateLinkRequest;
import com.kryxhub.kryxhub.dto.InitiateLinkResponse;
import com.kryxhub.kryxhub.entity.LinkedSocialAccountEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.Platforms;
import com.kryxhub.kryxhub.enums.SocialAccountStatus;
import com.kryxhub.kryxhub.repository.LinkedSocialAccountRepository;
import com.kryxhub.kryxhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ConnectedAppService {

    private final UserRepository userRepository;
    private final LinkedSocialAccountRepository linkedSocialAccountRepository;
    private final PlatformVerificationService platformVerificationService; // Inject the Router!

    public ConnectedAppService(UserRepository userRepository,
                               LinkedSocialAccountRepository linkedSocialAccountRepository,
                               PlatformVerificationService platformVerificationService) {
        this.userRepository = userRepository;
        this.linkedSocialAccountRepository = linkedSocialAccountRepository;
        this.platformVerificationService = platformVerificationService;
    }

    public InitiateLinkResponse initiateAccountLink(String email, InitiateLinkRequest request) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Platforms platformEnum = Platforms.valueOf(request.getPlatform().toUpperCase());

        boolean alreadyExists = user.getSocialAccounts().stream().anyMatch(acc -> acc.getPlatform() == platformEnum && acc.getPlatformUsername().equalsIgnoreCase(request.getPlatformUsername()));

        if (alreadyExists) {
            throw new RuntimeException("This account is already linked or pending verification.");
        }

        String generatedCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        LinkedSocialAccountEntity newLink = new LinkedSocialAccountEntity();
        newLink.setUser(user);
        newLink.setPlatform(platformEnum);
        newLink.setPlatformUsername(request.getPlatformUsername());
        newLink.setVerificationCode(generatedCode);
        newLink.setVerified(false);
        newLink.setStatus(SocialAccountStatus.LINKED);
        newLink.setCreatedAt(OffsetDateTime.now());

        newLink = linkedSocialAccountRepository.save(newLink);

        return new InitiateLinkResponse(newLink.getId(), newLink.getVerificationCode());
    }

    public boolean confirmAccountLink(String email, UUID linkedAccountId) {
        LinkedSocialAccountEntity linkedAccount = linkedSocialAccountRepository.findById(linkedAccountId).orElseThrow(() -> new RuntimeException("Linked account request not found."));

        if (!linkedAccount.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to verify this account.");
        }

        if (linkedAccount.getVerified()) {
            return true;
        }

        boolean isBioVerified = platformVerificationService.verifyCodeInBio(
                linkedAccount.getPlatform(),
                linkedAccount.getPlatformUsername(),
                linkedAccount.getVerificationCode()
        );

        if (isBioVerified) {
            linkedAccount.setVerified(true);
            linkedAccount.setVerifiedAt(OffsetDateTime.now());
            linkedSocialAccountRepository.save(linkedAccount);
            return true;
        }

        return false;
    }
}