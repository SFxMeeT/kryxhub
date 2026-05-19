package com.kryxhub.kryxhub.submission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kryxhub.kryxhub.submission.entity.SubmissionAnswerEntity;

import java.util.UUID;

@Repository
public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswerEntity, UUID> {
}
