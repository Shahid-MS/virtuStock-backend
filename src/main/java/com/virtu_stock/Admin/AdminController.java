package com.virtu_stock.Admin;

import java.util.List;
import java.util.Map;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.Helper.IPOHelper;
import com.virtu_stock.IPO.IPO;
import com.virtu_stock.IPO.IPORepository;
import com.virtu_stock.IPO.IPOService;
import com.virtu_stock.IPOAlerts.IPOAlertsService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    IPORepository ipoRepo;

    @Autowired
    IPOAlertsService ipoAlertsService;

    @Autowired
    private IPOHelper ipoHelper;

    @Autowired
    private IPOService ipoService;

    @SuppressWarnings("unchecked")
    @GetMapping("/ipo/fetch")
    public ResponseEntity<String> fetchIPO(
            @RequestParam(required = false, defaultValue = "open") String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int limit) {

        try {
            Map<String, Object> ipoAlertsResponse = ipoAlertsService.getIPOs(status, type, page, limit);
            Map<String, Object> meta = (Map<String, Object>) ipoAlertsResponse.get("meta");
            Integer totalPages = (Integer) meta.get("totalPages");

            for (int i = 1; i <= totalPages; i++) {
                Map<String, Object> ipoAlertsRes = ipoAlertsService.getIPOs(status, type, i, limit);
                List<Map<String, Object>> ipos = (List<Map<String, Object>>) ipoAlertsRes.get("ipos");
                for (Map<String, Object> ipoRes : ipos) {
                    String ipoAlertId = (String) ipoRes.get("id");
                    if ("DEBT".equals((String) ipoRes.get("type")) || ipoRepo.existsByIpoAlertId(ipoAlertId)) {
                        continue;
                    }
                    IPO ipo = ipoHelper.mapToIPO(ipoRes);
                    ipoRepo.save(ipo);
                }
            }
            return ResponseEntity.ok("All IPOs fetched and saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching IPOs: " + e.getMessage());
        }

    }

    @PutMapping("/ipo/{id}")
    public ResponseEntity<?> updateIpo(@PathVariable UUID id, @RequestBody IPO ipo) {
        try {
            IPO updatedIpo = ipoService.updateIpo(id, ipo);
            return ResponseEntity.ok().body(updatedIpo);

        } catch (Exception e) {
            // e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching IPOs: " + e.getMessage());
        }
    }
}
