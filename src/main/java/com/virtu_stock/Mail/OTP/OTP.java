package com.virtu_stock.Mail.OTP;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OTP {
    @Id
    private String email;
    
    private String otp;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private OTPPurpose purpose;
    private boolean isVerified = false;
}
