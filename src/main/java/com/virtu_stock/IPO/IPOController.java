package com.virtu_stock.IPO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.IPOAlerts.IPOAlertsService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class IPOController {

    @Autowired
    IPOJpaRepository ipoJPARepo;
    @Autowired
    IPOAlertsService ipoAlertsService;

    private IPO mapToIPO(Map<String, Object> ipoMap) {
        IPO ipo = new IPO();
        ipo.setName((String) ipoMap.get("name"));
        ipo.setSymbol((String) ipoMap.get("symbol"));
        ipo.setType((String) ipoMap.get("type"));
        ipo.setStatus((String) ipoMap.get("status"));

        ipo.setInfoUrl((String) ipoMap.get("infoUrl"));
        ipo.setNseInfoUrl((String) ipoMap.get("nseInfoUrl"));

        ipo.setLogo((String) ipoMap.get("logo"));
        ipo.setIssueSize((String) ipoMap.get("issueSize"));
        ipo.setProspectusUrl((String) ipoMap.get("prospectusUrl"));
        ipo.setAbout((String) ipoMap.get("about"));

        // strengths
        ipo.setStrengths((List<String>) ipoMap.get("strengths"));

        // risks
        ipo.setRisks((List<String>) ipoMap.get("risks"));
        return ipo;
    }

    @GetMapping("/admin/ipos")
    public List<IPO> createIPO(@RequestParam String status,
            @RequestParam(required = false, defaultValue = "open") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int limit) {
        Map<String, Object> ipoAlertsResponse = ipoAlertsService.getIPOs(status, type, page, limit);
        @SuppressWarnings("unchecked")
        Map<String, Object> meta = (Map<String, Object>) ipoAlertsResponse.get("meta");
        Integer totalPages = (Integer) meta.get("totalPages");

        for (int i = 1; i <= totalPages; i++) {
            Map<String, Object> ipoAlertsRes = ipoAlertsService.getIPOs(status, type, page, limit);
            IPO ipo = mapToIPO(ipoMap);
            ipoJPARepo.save(ipo);
            savedIPOs.add(ipo);
        }

        List<IPO> savedIPOs = new ArrayList<>();
        for (Map<String, Object> ipoMap : ipos) {
            IPO ipo = mapToIPO(ipoMap);
            ipoJPARepo.save(ipo);
            savedIPOs.add(ipo);
        }
        return savedIPOs;
    }

}
