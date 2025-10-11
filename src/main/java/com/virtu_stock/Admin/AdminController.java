package com.virtu_stock.Admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.DTO.IPOAlertsDTO;
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
    public ResponseEntity<Map<String, Object>> fetchIPO(
            @RequestParam(required = false, defaultValue = "open") String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int limit) {
        try {
            List<IPOAlertsDTO.Saved> saved = new ArrayList<>();
            List<IPOAlertsDTO.Skipped> skipped = new ArrayList<>();
            List<IPOAlertsDTO.Exists> exists = new ArrayList<>();
            List<IPOAlertsDTO.Error> errors = new ArrayList<>();
            Map<String, Object> ipoAlertsResponse = ipoAlertsService.getIPOs(status, type, page, limit);
            Map<String, Object> meta = (Map<String, Object>) ipoAlertsResponse.get("meta");
            Integer totalPages = (Integer) meta.get("totalPages");

            for (int i = 1; i <= totalPages; i++) {
                Map<String, Object> ipoAlertsRes = ipoAlertsService.getIPOs(status, type, i, limit);
                List<Map<String, Object>> ipos = (List<Map<String, Object>>) ipoAlertsRes.get("ipos");
                for (Map<String, Object> ipoRes : ipos) {
                    String ipoAlertId = (String) ipoRes.get("id");
                    String ipoName = (String) ipoRes.get("name");
                    if ("DEBT".equals((String) ipoRes.get("type"))) {
                        skipped.add(new IPOAlertsDTO.Skipped(ipoAlertId, ipoName, "Debt type"));
                        continue;
                    }
                    if (ipoRepo.existsByIpoAlertId(ipoAlertId)) {
                        exists.add(new IPOAlertsDTO.Exists(ipoAlertId, ipoName));
                        continue;
                    }
                    try {
                        IPO ipo = ipoHelper.mapToIPO(ipoRes);
                        ipoRepo.save(ipo);
                        saved.add(new IPOAlertsDTO.Saved(ipoAlertId, ipoName));
                    } catch (Exception e) {
                        errors.add(new IPOAlertsDTO.Error(ipoAlertId, ipoName, e.getMessage()));
                    }
                }
            }

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("Total", totalPages);
            res.put("Total Saved", saved.size());
            res.put("Total Exists", exists.size());
            res.put("Total Skipped", skipped.size());
            res.put("Total Errors", errors.size());
            res.put("Saved Ipos", saved);
            res.put("Exists Ipos", exists);
            res.put("Skipped Ipos", skipped);
            res.put("Errors Ipos", errors);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching IPOs");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @PutMapping("/ipo/{id}")
    public ResponseEntity<?> updateIpo(@PathVariable UUID id, @RequestBody IPO ipo) {
        try {
            IPO updatedIpo = ipoService.updateIpo(id, ipo);
            return ResponseEntity.ok().body(updatedIpo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching IPOs: " + e.getMessage());
        }
    }

    @DeleteMapping("/ipo/{id}")
    public ResponseEntity<?> DeleteIpo(@PathVariable UUID id) {
        try {
            ipoService.deleteIpo(id);
            return ResponseEntity.ok().body("IPO Deleted with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error Deleting: " + e.getMessage());
        }
    }
}
