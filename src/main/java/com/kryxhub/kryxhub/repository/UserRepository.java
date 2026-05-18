package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.dto.TopFunderDto;

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

    @Query(
        "SELECT new com.kryxhub.kryxhub.dto.TopFunderDto(" +
        "u.id, u.profilePicUrl, u.displayName, u.bio, COUNT(c)) " +
        "FROM UserEntity u JOIN u.campaigns c " +
        // WHERE c.status = 'ACTIVE'
        "GROUP BY u.id, u.profilePicUrl, u.displayName, u.bio " +
        "ORDER BY COUNT(c) DESC"
    )
    List<TopFunderDto> findTopFunders(Pageable pageable);
}
