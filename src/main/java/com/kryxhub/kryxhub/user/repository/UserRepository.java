package com.kryxhub.kryxhub.user.repository;

import com.kryxhub.kryxhub.analytics.dto.TopFunderDto;
import com.kryxhub.kryxhub.user.entity.UserEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT new com.kryxhub.kryxhub.analytics.dto.TopFunderDto(u.id, u.profilePicUrl, u.displayName, u.bio, COUNT(c)) " + "FROM UserEntity u JOIN u.campaigns c GROUP BY u.id, u.profilePicUrl, u.displayName, u.bio ORDER BY COUNT(c) DESC")
    List<TopFunderDto> findTopFunders(Pageable pageable);
}
