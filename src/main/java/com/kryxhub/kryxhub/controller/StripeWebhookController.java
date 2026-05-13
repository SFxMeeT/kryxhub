package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.service.StripePaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final StripePaymentService paymentService;

    public StripeWebhookController(StripePaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.err.println("Webhook security alert: Invalid signature!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            System.err.println("Webhook parsing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payload error");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            com.stripe.model.checkout.Session stripeSession = null;

            try {
                if (dataObjectDeserializer.getObject().isPresent()) {
                    stripeSession = (com.stripe.model.checkout.Session) dataObjectDeserializer.getObject().get();
                } else {
                    stripeSession = (com.stripe.model.checkout.Session) dataObjectDeserializer.deserializeUnsafe();
                }
            } catch (com.stripe.exception.EventDataObjectDeserializationException e) {
                System.err.println("Webhook deserialization failed: " + e.getMessage());
            }
            
            if (stripeSession != null && stripeSession.getMetadata() != null) {
                String campaignId = stripeSession.getMetadata().get("campaign_id");
                
                if (campaignId != null) {
                    System.out.println("Webhook caught for Campaign ID: " + campaignId);
                    paymentService.fulfillCampaignPayment(campaignId);
                } else {
                    System.err.println("Webhook caught, but no campaign_id was found in metadata.");
                }
            }
        }

        return ResponseEntity.ok("Success");
    }
}