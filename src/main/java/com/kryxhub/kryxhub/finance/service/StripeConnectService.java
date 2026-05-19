package com.kryxhub.kryxhub.finance.service;

import com.kryxhub.kryxhub.user.entity.UserEntity;
import com.kryxhub.kryxhub.user.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.model.Transfer;
import com.stripe.param.TransferCreateParams;
import java.math.BigDecimal;

@Service
public class StripeConnectService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final UserRepository userRepository;

    public StripeConnectService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String createConnectAccount(UserEntity creator) throws StripeException {
        if (creator.getStripeAccountId() != null && !creator.getStripeAccountId().isEmpty()) {
            return creator.getStripeAccountId();
        }

        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setCountry("US")
                .setEmail(creator.getEmail())
                .setCapabilities(
                        AccountCreateParams.Capabilities.builder()
                                .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                                .build()
                )
                .build();

        Account account = Account.create(params);

        creator.setStripeAccountId(account.getId());
        userRepository.save(creator);

        return account.getId();
    }

    public String generateOnboardingLink(String stripeAccountId) throws StripeException {
        String baseUrl = "https://kryxhub-api.onrender.com"; 

        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                .setAccount(stripeAccountId)
                .setRefreshUrl(baseUrl + "/api/payouts/onboard/refresh")
                .setReturnUrl(baseUrl + "/api/payouts/onboard/return")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(params);
        return accountLink.getUrl();
    }

    public String transferToCreator(String connectedAccountId, BigDecimal amountNet) throws StripeException {
        

        long amountInCents = amountNet.multiply(new BigDecimal("100")).longValue();

        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setDestination(connectedAccountId)
                .setDescription("KryxHub Campaign Payout")
                .build();

        Transfer transfer = Transfer.create(params);
        
        return transfer.getId(); 
    }
}