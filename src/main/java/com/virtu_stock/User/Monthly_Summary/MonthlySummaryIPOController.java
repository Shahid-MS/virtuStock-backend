package com.virtu_stock.User.Monthly_Summary;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.IPO.IPOService;
import com.virtu_stock.User.User;
import com.virtu_stock.User.UserService;
import com.virtu_stock.User.Alloted_IPOs.AllotedIpoService;
import com.virtu_stock.User.Applied_IPOs.AppliedIpoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MonthlySummaryIPOController {
    private final UserService userService;
    private final IPOService ipoService;
    private final AppliedIpoService appliedIpoService;
    private final AllotedIpoService allotedIpoService;

    @GetMapping("/monthly-summary")
    public ResponseEntity<?> monthlySummary(Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        int[] total = new int[12];
        int[] applied = new int[12];
        int[] alloted = new int[12];

        Map<String, List<Object[]>> res = new HashMap<>();
        List<Object[]> countIpoByMonth = ipoService.getIpoCountByMonth();
        res.put("All ipo", countIpoByMonth);

        List<Object[]> countAppliedIpo = appliedIpoService.countAppliedByUserAndMonth(user.getId());
        res.put("Applied", countAppliedIpo);
        return ResponseEntity.ok(res);

        
        

    }
}