package com.virtu_stock.Mail.OTP;

import java.time.LocalDateTime;

import java.util.Optional;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.virtu_stock.Mail.MailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final OTPRepository otpRepository;
    private final MailService mailService;
    private final Random random = new Random();

    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        OTP otpEntity = new OTP(email, otp, LocalDateTime.now(), false);
        mailService.sendOTPForRegistration(email, otp);
        otpRepository.save(otpEntity);
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OTP> optionalOtp = otpRepository.findById(email);
        if (optionalOtp.isEmpty())
            return false;

        OTP otpEntity = optionalOtp.get();

        if (otpEntity.isVerified())
            return true;
        if (!otpEntity.getOtp().equals(otp))
            return false;
        if (otpEntity.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            otpRepository.deleteById(email);
            return false;
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);
        return true;
    }

    public boolean isEmailVerified(String email) {
        return otpRepository.findById(email)
                .map(OTP::isVerified)
                .orElse(false);
    }

    public void deleteOtpByEmail(String email) {
        otpRepository.deleteById(email);
    }

    @Transactional
    // run every 1 hr
    @Scheduled(fixedRate = 3600000)
    public void clearExpiredOtps() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
        otpRepository.deleteAllByCreatedAtBefore(cutoff);
    }

}
