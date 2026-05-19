package com.kryxhub.kryxhub.core.config;

import com.kryxhub.kryxhub.campaign.entity.PlatformSettingsEntity;
import com.kryxhub.kryxhub.campaign.repository.PlatformSettingsRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PlatformSettingsSeeder implements CommandLineRunner {

    private final PlatformSettingsRepository settingsRepository;

    public PlatformSettingsSeeder(PlatformSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (settingsRepository.count() == 0) {
            PlatformSettingsEntity defaultSettings = new PlatformSettingsEntity();
            defaultSettings.setId(1L);
            defaultSettings.setPlatformFeeRate(new BigDecimal("0.10"));
            settingsRepository.save(defaultSettings);
            System.out.println("Default Platform Settings initialized (Fee: 10%)");
        }
    }
}