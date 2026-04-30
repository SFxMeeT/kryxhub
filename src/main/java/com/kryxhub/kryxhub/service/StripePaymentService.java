package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.entity.CampaignEntity;
import com.kryxhub.kryxhub.repository.CampaignRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class StripePaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final CampaignRepository campaignRepository;

    public StripePaymentService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String createCheckoutSession(UUID campaignId, String funderEmail) throws Exception {
        
        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!campaign.getFunder().getEmail().equals(funderEmail)) {
            throw new RuntimeException("Unauthorized: You do not own this campaign.");
        }

        if (!"DRAFT".equals(campaign.getStatus())) {
            throw new RuntimeException("This campaign is already funded or active.");
        }

        long amountInCents = campaign.getTotalBudget().multiply(new BigDecimal("100")).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://kryxhub-api.onrender.com/payment-success.html")
                .setCancelUrl("https://kryxhub-api.onrender.com/payment-cancelled.html")
                .putMetadata("campaign_id", campaign.getId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Fund Campaign: " + campaign.getTitle())
                                                                .setDescription("KryxHub Campaign Budget Lock")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}