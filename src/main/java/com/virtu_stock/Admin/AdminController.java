package com.virtu_stock.Admin;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.virtu_stock.DTO.IPOAlertsDTO;
import com.virtu_stock.Enum.Verdict;
import com.virtu_stock.GMP.GMP;
import com.virtu_stock.Helper.IPOHelper;
import com.virtu_stock.IPO.IPO;
import com.virtu_stock.IPO.IPORepository;
import com.virtu_stock.IPO.IPOService;
import com.virtu_stock.IPO.IssueSize;
import com.virtu_stock.IPOAlerts.IPOAlertsService;
import com.virtu_stock.Subscription.Subscription;

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
    public ResponseEntity<?> updateIpo(@PathVariable UUID id, @RequestBody JsonNode ipoNode) {

        try {
            IPO existingIpo = ipoService.getIpoById(id);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            ipoNode.fieldNames().forEachRemaining(fieldName -> {

                switch (fieldName.toLowerCase()) {
                    case "subscriptions":
                        List<Subscription> newSubs = Arrays.asList(
                                mapper.convertValue(ipoNode.get(fieldName), Subscription[].class));
                        ipoService.updateSubscriptions(existingIpo, newSubs);
                        break;

                    case "gmp":
                        List<GMP> newGmp = Arrays.asList(
                                mapper.convertValue(ipoNode.get(fieldName), GMP[].class));
                        ipoService.updateGmp(existingIpo, newGmp);
                        break;

                    case "verdict":
                        String verdictStr = ipoNode.get(fieldName).asText();
                        try {
                            Verdict newVerdict = Verdict.valueOf(verdictStr.toUpperCase());
                            ipoService.updateVerdict(existingIpo, newVerdict);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Invalid Verdict Value: " + verdictStr);
                        }
                        break;

                    case "issuesize":
                        IssueSize newIssueSize = mapper.convertValue(ipoNode.get(fieldName), IssueSize.class);
                        ipoService.updateIssueSize(existingIpo, newIssueSize);
                        break;

                }
            });

            IPO updatedIpo = ipoService.saveIpo(existingIpo);
            return ResponseEntity.ok(updatedIpo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating IPO: " + e.getMessage());
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

    @GetMapping("/ipo")
    public List<IPO> fetchIPO() {
        return ipoService.fetchIPOByListingPending();
    }

}
