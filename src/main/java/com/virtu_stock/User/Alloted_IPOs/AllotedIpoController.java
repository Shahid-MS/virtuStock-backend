package com.virtu_stock.User.Alloted_IPOs;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.Enum.AllotmentStatus;
import com.virtu_stock.User.User;
import com.virtu_stock.User.UserService;
import com.virtu_stock.User.Applied_IPOs.AppliedIpo;
import com.virtu_stock.User.Applied_IPOs.AppliedIpoResponseDTO;
import com.virtu_stock.User.Applied_IPOs.AppliedIpoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class AllotedIpoController {
    private final AppliedIpoService appliedIpoService;
    private final AllotedIpoService allotedIpoService;
    private final ModelMapper modelMapper;

    private final UserService userService;

    // @PostMapping("/alloted/{appliedIpoId}")
    // public ResponseEntity<?> alloted(@PathVariable UUID appliedIpoId) {

    // try {
    // boolean alreadyAlloted =
    // allotedIpoService.existsByAppliedIpoId(appliedIpoId);
    // if (alreadyAlloted) {
    // return ResponseEntity.badRequest().body("IPO is already Alloted");
    // }
    // AppliedIpo appliedIpo = appliedIpoService.findById(appliedIpoId);
    // appliedIpo.setAllotment(AllotmentStatus.ALLOTED);
    // AppliedIpo savedAppliedIpo = appliedIpoService.save(appliedIpo);
    // AllotedIpo allotedIpo = new AllotedIpo();
    // allotedIpo.setAppliedIpo(savedAppliedIpo);
    // allotedIpo.setAllotedLot(1);
    // allotedIpo.setSellPrice(null);
    // allotedIpo.setTaxDeducted(0.0);
    // AllotedIpo savedAllotedIpo = allotedIpoService.save(allotedIpo);
    // AllotedIPOResponseDTO res = modelMapper.map(savedAllotedIpo,
    // AllotedIPOResponseDTO.class);
    // return ResponseEntity.status(HttpStatus.CREATED).body(res);
    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body("message: " + e.getMessage());
    // }
    // }

    @PatchMapping("/alloted-ipo/{id}")
    public ResponseEntity<?> updateById(@PathVariable UUID id, Principal principal,
            @RequestBody Map<String, Object> req) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            AllotedIpo allotedIpo = allotedIpoService.findById(id);

            if (!allotedIpo.getAppliedIpo().getUser().getId().equals(user.getId())) {
                throw new RuntimeException("User does not own this IPO: " + id);
            }

            for (String key : req.keySet()) {
                switch (key) {
                    case "lot" -> {
                        Integer lot = (Integer) req.get("lot");

                        if (lot == null || lot < 1) {
                            return ResponseEntity.badRequest().body("Invalid lot value");
                        }

                        if (!lot.equals(allotedIpo.getAllotedLot())) {
                            allotedIpo.setAllotedLot(lot);
                        }
                    }
                    case "sellPrice" -> {
                        Object sellPriceObj = req.get("sellPrice");
                        Double sellPrice = sellPriceObj == null ? null : Double.parseDouble(sellPriceObj.toString());
                        if (allotedIpo.getSellPrice() != sellPrice) {
                            allotedIpo.setSellPrice(sellPrice);
                        }
                    }
                    default -> {
                        return ResponseEntity.badRequest()
                                .body("Invalid field: " + key);
                    }
                }
            }
            allotedIpoService.save(allotedIpo);
            AllotedIPOResponseDTO res = modelMapper.map(allotedIpo, AllotedIPOResponseDTO.class);
            return ResponseEntity.ok(res);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Please login and try again");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong" + e.getMessage());
        }
    }

}
