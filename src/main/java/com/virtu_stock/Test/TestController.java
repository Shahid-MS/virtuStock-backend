package com.virtu_stock.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.Mail.MailService;
import com.virtu_stock.Security.Util.AuthUtil;
import com.virtu_stock.User.User;
import com.virtu_stock.User.UserRepository;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> fetchIPO() {
        try {
            Map<String, Object> res = new LinkedHashMap<>();

            // Summary counts
            res.put("Total", 4);
            res.put("Total Saved", 1);
            res.put("Total Exists", 1);
            res.put("Total Skipped", 1);
            res.put("Total Errors", 1);

            // Saved IPOs
            res.put("Saved Ipos", List.of(
                    Map.of(
                            "id", "1783887536",
                            "name", "Studds Accessories Limited","type","SME")));

            // Exists IPOs
            res.put("Exists Ipos", List.of(
                    Map.of(
                            "id", "1055223847",
                            "name", "Lenskart Solutions Limited")));

            // Skipped IPOs
            res.put("Skipped Ipos", List.of(
                    Map.of(
                            "id", "1055223847",
                            "name", "Jordon Solutions Limited",
                            "reason", "Debt Type")));

            // Errors IPOs
            res.put("Errors Ipos", List.of(
                    Map.of(
                            "id", "1055223847",
                            "name", "Groww",
                            "message", "Api error")));

            String to = AuthUtil.getCurrentUserEmail();
            mailService.sendIpoFetchSummaryEmail(to, res);
            return ResponseEntity.ok(res);

        } catch (

        Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching IPOs");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email");
        }
        User user = userOptional.get();
        userRepository.delete(user);

        return ResponseEntity.ok("User deleted Successfully");
    }
}
