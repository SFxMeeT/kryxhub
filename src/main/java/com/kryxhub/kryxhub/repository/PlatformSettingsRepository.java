package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.PlatformSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformSettingsRepository extends JpaRepository<PlatformSettingsEntity, Long> {
}