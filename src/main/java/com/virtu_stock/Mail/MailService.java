package com.virtu_stock.Mail;

import java.time.LocalDate;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendHtmlMail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.addInline("logo", new ClassPathResource("static/Images/logo/logo-name.png"));
            mailSender.send(mimeMessage);

        } catch (MailSendException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid email address: " + to);
        } catch (MailException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Failed to send email to " + to + ". Please try again later.");
        } catch (MessagingException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Email formatting error.");
        }
    }

    public boolean hasMXRecord(String email) {
        try {
            String domain = email.substring(email.indexOf('@') + 1);
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[] { "MX" });
            return attrs != null && attrs.get("MX") != null;
        } catch (NamingException e) {
            return false;
        }
    }

    public void sendOTPForRegistration(String to, String otp) {
        String subject = "OTP for Registration";
        String htmlContent = """
                                                <div style="font-family: 'Segoe UI', Arial, sans-serif; background-color: #f9fafc; padding: 30px;">
                  <div style="max-width: 500px; margin: 0 auto; background: white; border-radius: 10px;
                              box-shadow: 0 2px 6px rgba(0,0,0,0.1); padding: 25px; text-align: center;">
                    <img src="cid:logo" alt="VirtuStock" style="width:140px; margin-bottom:20px;">
                    <h2 style="color:#1d2939;">Verify Your Email</h2>
                    <p style="color:#555;">To complete your registration, please use the OTP below:</p>
                    <h1 style="color:#007bff; letter-spacing: 3px; margin: 20px 0;">%s</h1>
                    <p style="color:#555;">This OTP is valid for <strong>5 minutes</strong>. Please don’t share it with anyone.</p>
                    <p style="margin-top:30px; color:#999; font-size:12px;">If you didn’t request this, please ignore this email.</p>
                    <p style="color:#999; font-size:12px; margin-top:20px;">&copy; %s VirtuStock. All rights reserved.</p>
                  </div>
                </div>
                                            """
                .formatted(otp, LocalDate.now().getYear());
        sendHtmlMail(to, subject, htmlContent);
    }

    public void sendWelcomeEmail(String to, String name) {
        String subject = "Welcome to VirtuStock 🎉";
        String htmlContent = """
                                   <div style="font-family: 'Segoe UI', Arial, sans-serif; background-color: #f9fafc; padding: 30px;">
                  <div style="max-width: 500px; margin: 0 auto; background: white; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); padding: 25px; text-align: center;">
                    <img src="cid:logo" alt="VirtuStock" style="width:140px; margin-bottom:20px;">
                    <h2 style="color:#1d2939;">Welcome to VirtuStock!</h2>
                    <p style="color:#555;">Hi <b>%s</b>, we're thrilled to have you join our investing community.</p>
                    <p style="color:#555;">You’re all set to explore stock trends, analyze IPOs and make smarter investment decisions.</p>
                    <a href="https://virtustock.in/login" style="display:inline-block; margin-top:15px; background:#1d2939; color:white; text-decoration:none; padding:10px 25px; border-radius:6px;">Get Started</a>
                    <p style="color:#999; font-size:12px; margin-top:30px;">&copy; %s VirtuStock. All rights reserved.</p>
                  </div>
                </div>
                                """
                .formatted(name.split(" ")[0], LocalDate.now().getYear());
        sendHtmlMail(to, subject, htmlContent);
    }

    public void sendOTPForForgotPassword(String to, String otp) {
        String subject = "Account Recovery - VirtuStock";

        String htmlContent = """
                    <div style="font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f6f8; padding: 30px;">
                      <div style="max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 10px;
                                  box-shadow: 0 2px 8px rgba(0,0,0,0.1); padding: 25px; text-align: center;">
                        <img src="cid:logo" alt="VirtuStock" style="width:140px; margin-bottom:20px;">
                        <h2 style="color:#1d2939;">Reset Your Password</h2>
                        <p style="color:#555;">We received a request to reset your VirtuStock account password.</p>
                        <p style="color:#555;">Use the following One-Time Password (OTP) to proceed:</p>
                        <h1 style="color:#007bff; letter-spacing: 3px; margin: 20px 0;">%s</h1>
                        <p style="color:#555;">This OTP is valid for <strong>5 minutes</strong>.
                           Please do not share it with anyone.</p>
                        <p style="margin-top:30px; color:#999; font-size:12px;">If you didn’t request a password reset,
                           you can safely ignore this email — your account is secure.</p>
                        <p style="color:#999; font-size:12px; margin-top:20px;">
                            &copy; %s VirtuStock. All rights reserved.
                        </p>
                      </div>
                    </div>
                """.formatted(otp, LocalDate.now().getYear());

        sendHtmlMail(to, subject, htmlContent);
    }

    public void sendPasswordResetMail(String to, String name) {
        String subject = "Account Secure - Password Reset Successful";
        String htmlContent = """
                                                 <div style="font-family: 'Segoe UI', Arial, sans-serif; background-color: #f9fafc; padding: 30px;">
                  <div style="max-width: 500px; margin: 0 auto; background: white; border-radius: 10px;
                              box-shadow: 0 2px 6px rgba(0,0,0,0.1); padding: 25px; text-align: center;">
                    <img src="cid:logo" alt="VirtuStock" style="width:140px; margin-bottom:20px;">
                    <h2 style="color:#1d2939;">Your Password Has Been Reset</h2>
                    <p style="color:#555;">Hi <b>%s</b>, your VirtuStock account password was successfully updated.</p>
                    <p style="color:#555;">If you made this change, you can safely ignore this message.</p>
                    <p style="color:#555;">If you didn’t reset your password, please <a href="https://virtustock.in/forgot-password" style="color:#007bff; text-decoration:none;">reset it again</a> immediately or contact our support team.</p>
                    <a href="https://virtustock.in/login" style="display:inline-block; margin-top:20px; background:#1d2939; color:white; text-decoration:none; padding:10px 25px; border-radius:6px;">Login to VirtuStock</a>
                    <p style="color:#999; font-size:12px; margin-top:30px;">&copy; %s VirtuStock. All rights reserved.</p>
                  </div>
                </div>

                                                """
                .formatted(name.split(" ")[0], LocalDate.now().getYear());
        sendHtmlMail(to, subject, htmlContent);
    }

}
