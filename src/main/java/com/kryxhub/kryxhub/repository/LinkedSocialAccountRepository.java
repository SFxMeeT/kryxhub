package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.LinkedSocialAccountEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.enums.Platforms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LinkedSocialAccountRepository extends JpaRepository<LinkedSocialAccountEntity, UUID> {

    boolean existsByUserAndPlatformAndIsVerified(UserEntity user, Platforms platform, Boolean isVerified);

}