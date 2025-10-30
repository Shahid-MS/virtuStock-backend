package com.virtu_stock.Security.Authentication;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// import com.virtu_stock.Mail.MailBoxLayer.EmailVerificationService;
import com.virtu_stock.Mail.MailService;
import com.virtu_stock.Mail.OTP.OTPPurpose;
import com.virtu_stock.Mail.OTP.OTPService;
import com.virtu_stock.Security.Authentication.DTO.AuthRequest;
import com.virtu_stock.Security.Authentication.DTO.ResetPasswordRequestDTO;
import com.virtu_stock.Security.JWT.JWTUtil;
import com.virtu_stock.User.CustomUserDetailsService;
import com.virtu_stock.User.User;
import com.virtu_stock.User.UserResponseDTO;
import com.virtu_stock.User.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final OTPService otpService;
    // private final EmailVerificationService emailVerificationService;
    private final MailService mailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Authenticate user credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            final var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(Map.of("token", jwt));

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));

        } catch (DisabledException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "User account is disabled"));

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        try {
            if (result.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            if (!otpService.isEmailVerified(user.getEmail(), OTPPurpose.SIGN_UP)) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Please verify your email before registration."));
            }

            User savedUser = userService.registerUser(user);
            otpService.deleteByEmailAndPurpose(user.getEmail(), OTPPurpose.SIGN_UP);
            mailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
            UserResponseDTO userResponseDTO = modelMapper.map(savedUser, UserResponseDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User Saved Successfully", "User", userResponseDTO));

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("error", ex.getReason()));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email is required"));
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid email format"));
        }

        if (!mailService.hasMXRecord(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid email domain. Please enter a valid email address."));
        }

        if (userService.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "User Already exists"));
        }

        // if (!emailVerificationService.verifyEmail(email)) {
        // return ResponseEntity.badRequest()
        // .body(Map.of("error", "Invalid email. Please enter a valid email address."));
        // }
        try {
            otpService.generateAndSendOtp(email, OTPPurpose.SIGN_UP);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + email));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("error", ex.getReason()));
        } catch (MailException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Failed to send OTP to the provided email. Please check the email address."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while sending OTP."));
        }
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Email and OTP are required"));

        boolean verified = otpService.verifyOtp(email, otp, OTPPurpose.SIGN_UP);

        if (verified)
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid Email or expired OTP"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email is required"));
        }
        if (!userService.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No user found"));
        }
        try {
            otpService.generateAndSendOtp(email, OTPPurpose.FORGOT_PASSWORD);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + email));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("error", ex.getReason()));
        } catch (MailException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Failed to send OTP to the provided email. Please check the email address."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while sending OTP."));
        }
    }

    @PostMapping("/verify-forgot-otp")
    public ResponseEntity<?> verifyForgotOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Email and OTP are required"));

        boolean verified = otpService.verifyOtp(email, otp, OTPPurpose.FORGOT_PASSWORD);

        if (verified)
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid Email or expired OTP"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request, BindingResult result) {

        if (result.hasErrors()) {

            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        String email = request.getEmail();
        String password = request.getPassword();
        if (!otpService.isEmailVerified(email, OTPPurpose.FORGOT_PASSWORD)) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", "Please verify your email before reset password."));
        }

        try {

            User savedUser = userService.setPassword(email, password);

            otpService.deleteByEmailAndPurpose(savedUser.getEmail(), OTPPurpose.FORGOT_PASSWORD);
            mailService.sendPasswordResetMail(savedUser.getEmail(), savedUser.getFirstName());
            return ResponseEntity.ok(Map.of("message", "Password Succesfully updated"));

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not found"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "message", ex.getMessage()));
        }
    }

}
