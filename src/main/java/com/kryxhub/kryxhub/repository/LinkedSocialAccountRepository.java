package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.LinkedSocialAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LinkedSocialAccountRepository extends JpaRepository<LinkedSocialAccountEntity, UUID> {

    boolean existsByUserAndPlatformAndVerified(UserEntity user, Platforms platform, Boolean verified);

}
