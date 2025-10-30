package com.virtu_stock.Mail.OTP;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTP, String> {

    void deleteAllByCreatedAtBefore(LocalDateTime cutoff);

}
