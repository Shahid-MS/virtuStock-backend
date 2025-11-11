package com.virtu_stock.User.Alloted_IPOs;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtu_stock.Enum.AllotmentStatus;
import com.virtu_stock.User.Applied_IPOs.AppliedIpo;

import com.virtu_stock.User.Applied_IPOs.AppliedIpoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AllotedIpoController {
    private final AppliedIpoService appliedIpoService;
    private final AllotedIpoService allotedIpoService;
    private final ModelMapper modelMapper;

    @PostMapping("/alloted/{appliedIpoId}")
    public ResponseEntity<?> alloted(@PathVariable UUID appliedIpoId) {

        try {
            boolean alreadyAlloted = allotedIpoService.existsByAppliedIpoId(appliedIpoId);
            if (alreadyAlloted) {
                return ResponseEntity.badRequest().body("IPO is already Alloted");
            }
            AppliedIpo appliedIpo = appliedIpoService.findById(appliedIpoId);
            appliedIpo.setAllotment(AllotmentStatus.ALLOTED);
            AppliedIpo savedAppliedIpo = appliedIpoService.save(appliedIpo);
            AllotedIpo allotedIpo = new AllotedIpo();
            allotedIpo.setAppliedIpo(savedAppliedIpo);
            allotedIpo.setAllotedLot(1);
            allotedIpo.setSellPrice(null);
            allotedIpo.setTaxDeducted(0.0);
            AllotedIpo savedAllotedIpo = allotedIpoService.save(allotedIpo);
            AllotedIPOResponseDTO res = modelMapper.map(savedAllotedIpo, AllotedIPOResponseDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("message: " + e.getMessage());
        }
    }
}
