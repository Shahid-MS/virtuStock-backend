package com.virtu_stock.Helper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.virtu_stock.IPO.IPO;

@Component
public class IPOHelper {
    public IPO mapToIPO(Object ipoObj) {
        if (!(ipoObj instanceof Map<?, ?>)) {
            return null;
        }

        System.out.println(ipoObj);

        @SuppressWarnings("unchecked")
        Map<String, Object> ipoMap = (Map<String, Object>) ipoObj;

        IPO ipo = new IPO();
        ipo.setIpoAlertId((String) ipoMap.get("id"));
        ipo.setName((String) ipoMap.get("name"));
        ipo.setSymbol((String) ipoMap.get("symbol"));
        ipo.setType((String) ipoMap.get("type"));
        ipo.setStatus((String) ipoMap.get("status"));
        ipo.setInfoUrl((String) ipoMap.get("infoUrl"));
        ipo.setNseInfoUrl((String) ipoMap.get("nseInfoUrl"));

        ipo.setStartDate(LocalDate.parse((String) ipoMap.get("startDate")));
        ipo.setEndDate(LocalDate.parse((String) ipoMap.get("endDate")));
        ipo.setListingDate(LocalDate.parse((String) ipoMap.get("listingDate")));

        String[] priceRange = ((String) ipoMap.get("priceRange")).split("-");
        ipo.setMinPrice(Integer.parseInt(priceRange[0]));
        ipo.setMaxPrice(Integer.parseInt(priceRange[1]));

        ipo.setMinQty((Integer) ipoMap.get("minQty"));
        ipo.setLogo((String) ipoMap.get("logo"));
        ipo.setIssueSize((String) ipoMap.get("issueSize"));
        ipo.setProspectusUrl((String) ipoMap.get("prospectusUrl"));
        ipo.setAbout((String) ipoMap.get("about"));

        Object strengthsObj = ipoMap.get("strengths");
        ipo.setStrengths(((List<?>) strengthsObj).stream()
                .map(String::valueOf)
                .toList());

        Object risksObj = ipoMap.get("risks");
        ipo.setRisks(((List<?>) risksObj).stream()
                .map(String::valueOf)
                .toList());

        return ipo;
    }

}
