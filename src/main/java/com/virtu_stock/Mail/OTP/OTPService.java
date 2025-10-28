package com.virtu_stock.Mail.OTP;

import java.time.LocalDateTime;
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
        OTP otpEntity = new OTP(email, otp, LocalDateTime.now());
        String subject = "OTP for Registration";
        String htmlContent = """
                    <div style="font-family: Arial, sans-serif; color: #333;">
                        <div style="text-align:center;">
                            <img src="cid:logo" alt="VirtuStock" style="width:140px;margin-bottom:15px;">
                        </div>
                        <p>Your One-Time Password (OTP) for registration is:</p>
                        <h1 style="color:#007bff;">%s</h1>
                        <p>This OTP is valid for <strong>5 minutes</strong>.</p>
                        <br>
                        <p>Thank you,<br>MS 2.O & Team</p>
                    </div>
                """.formatted(otp);
        mailService.sendHtmlMail(email, subject, htmlContent);
        otpRepository.save(otpEntity);
    }

    // public boolean validateOtp(String email, String otp) {
    // Optional<UserOtp> otpRecord = otpRepository.findById(email);

    // if (otpRecord.isEmpty())
    // return false;

    // UserOtp userOtp = otpRecord.get();

    // // Check expiry
    // if (userOtp.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
    // otpRepository.deleteById(email);
    // return false;
    // }

    // boolean isValid = userOtp.getOtp().equals(otp);
    // if (isValid) {
    // otpRepository.deleteById(email); // delete OTP after success
    // }

    // return isValid;
    // }

    @Transactional
    // run every 1 hr
    @Scheduled(fixedRate = 3600000)
    public void clearExpiredOtps() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
        otpRepository.deleteAllByCreatedAtBefore(cutoff);
    }

}
