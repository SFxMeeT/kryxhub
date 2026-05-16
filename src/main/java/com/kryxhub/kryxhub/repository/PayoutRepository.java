package com.kryxhub.kryxhub.repository;

import com.kryxhub.kryxhub.entity.PayoutEntity;
import com.kryxhub.kryxhub.enums.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface PayoutRepository extends JpaRepository<PayoutEntity, UUID> {


    @Query("SELECT SUM(p.platformFee) FROM PayoutEntity p WHERE p.status = :status")
    BigDecimal sumPlatformRevenueByStatus(@Param("status") PayoutStatus status);

    @Query("SELECT SUM(p.amountGross) FROM PayoutEntity p WHERE p.status = :status")
    BigDecimal sumGrossProcessedByStatus(@Param("status") PayoutStatus status);

    long countByStatus(PayoutStatus status);
}
