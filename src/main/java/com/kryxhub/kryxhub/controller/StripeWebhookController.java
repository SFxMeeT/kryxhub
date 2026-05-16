package com.kryxhub.kryxhub.controller;

import com.kryxhub.kryxhub.service.StripePaymentService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String mainEndpointSecret;

    @Value("${stripe.connect.webhook.secret}")
    private String connectEndpointSecret;

    private final StripePaymentService paymentService;

    public StripeWebhookController(StripePaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe/main")
    public ResponseEntity<String> handleMainWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws EventDataObjectDeserializationException {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, mainEndpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payload error");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            com.stripe.model.checkout.Session stripeSession = null;

            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeSession = (com.stripe.model.checkout.Session) dataObjectDeserializer.getObject().get();
            } else {
                stripeSession = (com.stripe.model.checkout.Session) dataObjectDeserializer.deserializeUnsafe();
            }

            if (stripeSession != null && stripeSession.getMetadata() != null) {
                String campaignId = stripeSession.getMetadata().get("campaign_id");
                if (campaignId != null) {
                    paymentService.fulfillCampaignPayment(campaignId);
                }
            }
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/stripe/connect")
    public ResponseEntity<String> handleConnectWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, connectEndpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Connect signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Connect Payload error");
        }

        if ("transfer.failed".equals(event.getType())) {
            System.out.println("⚠️ A transfer to a creator failed! Event ID: " + event.getId());
        }

        return ResponseEntity.ok("Success");
    }
}