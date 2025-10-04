package com.virtu_stock.IPO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.virtu_stock.IPOAlerts.IPOAlertsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class IPOController {

    // @GetMapping("/ipo")
    // public String getIpo() {
    // return new
    // }
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
        ipo.setSlug((String) ipoMap.get("slug"));
        ipo.setInfoUrl((String) ipoMap.get("infoUrl"));
        ipo.setNseInfoUrl((String) ipoMap.get("nseInfoUrl"));
        ipo.setStartDate((String) ipoMap.get("startDate"));
        ipo.setEndDate((String) ipoMap.get("endDate"));
        ipo.setListingDate((String) ipoMap.get("listingDate"));
        ipo.setPriceRange((String) ipoMap.get("priceRange"));
        ipo.setMinQty((Integer) ipoMap.get("minQty"));
        ipo.setMinAmount((Integer) ipoMap.get("minAmount"));
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

    @GetMapping("/ipos")
    public List<IPO> createIPO(@RequestParam String status) {
        System.out.println("Create ipos");
        Map<String, Object> body = ipoAlertsService.getIPOs(status, null, 1, 1);
        List<Map<String, Object>> ipos = (List<Map<String, Object>>) body.get("ipos");

        List<IPO> savedIPOs = new ArrayList<>();
        for (Map<String, Object> ipoMap : ipos) {
            IPO ipo = mapToIPO(ipoMap);
            ipoJPARepo.save(ipo);
            savedIPOs.add(ipo);
        }
        return savedIPOs;
    }

}
