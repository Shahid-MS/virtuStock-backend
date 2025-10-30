package com.virtu_stock.Mail.OTP;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTP, String> {

    Optional<OTP> findByEmailAndPurpose(String email, OTPPurpose purpose);

    void deleteByEmailAndPurpose(String email, OTPPurpose purpose);

    void deleteAllByCreatedAtBefore(LocalDateTime cutoff);

}
