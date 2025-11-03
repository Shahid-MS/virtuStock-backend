package com.virtu_stock.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.Mail.MailService;
import com.virtu_stock.Security.Util.AuthUtil;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private MailService mailService;

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
                            "name", "Studds Accessories Limited")));

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
}
