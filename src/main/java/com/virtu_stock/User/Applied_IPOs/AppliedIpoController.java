package com.virtu_stock.User.Applied_IPOs;

import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.Enum.AllotmentStatus;
import com.virtu_stock.IPO.IPO;
import com.virtu_stock.IPO.IPOResponseDTO;
import com.virtu_stock.IPO.IPOService;
import com.virtu_stock.User.User;
import com.virtu_stock.User.UserService;
import com.virtu_stock.User.Alloted_IPOs.AllotedIpo;
import com.virtu_stock.User.Alloted_IPOs.AllotedIpoService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AppliedIpoController {
    private final UserService userService;
    private final IPOService ipoService;
    private final AppliedIpoService appliedIpoService;
    private final ModelMapper modelMapper;
    private final AllotedIpoService allotedIpoService;

    @PostMapping("/apply")
    public ResponseEntity<?> markAsApplied(@RequestBody AppliedIpoRequestDTO request, Principal principal) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);

            IPO ipo = ipoService.getIpoById(UUID.fromString(request.getIpoId()));

            boolean alreadyApplied = appliedIpoService.existsByUserAndIpo(user, ipo);
            if (alreadyApplied) {
                return ResponseEntity.badRequest().body("You have already applied for this IPO");
            }

            AppliedIpo appliedIpo = new AppliedIpo();
            appliedIpo.setUser(user);
            appliedIpo.setIpo(ipo);
            appliedIpo.setAppliedLot(request.getAppliedLot());
            appliedIpo.setAllotment(AllotmentStatus.NOT_ALLOTED);
            appliedIpoService.save(appliedIpo);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("Message", "Successfully created", "appliedIpoId", appliedIpo.getId()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Please Login and try again");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while marking IPO as applied ❌");
        }
    }

    @GetMapping("/check-applied-ipo")
    public ResponseEntity<?> checkApplied(
            Principal principal,
            @RequestParam String ipoId) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            IPO ipo = ipoService.getIpoById(UUID.fromString(ipoId));
            Optional<AppliedIpo> appliedIpo = appliedIpoService.findByUserAndIpo(user, ipo);
            Map<String, Object> res;
            if (appliedIpo.isPresent()) {
                res = Map.of(
                        "applied", true,
                        "appliedIpoId", appliedIpo.get().getId());
            } else {
                res = Map.of(
                        "applied", false);
            }
            return ResponseEntity.ok(res);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Please Login and try again");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Please try again later");
        }

    }

    @DeleteMapping("/unmark-as-applied")
    public ResponseEntity<?> unmarkApplied(
            Principal principal,
            @RequestParam String ipoId) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            IPO ipo = ipoService.getIpoById(UUID.fromString(ipoId));
            boolean isAppliedExist = appliedIpoService.existsByUserAndIpo(user, ipo);
            if (!isAppliedExist) {
                return ResponseEntity.badRequest().body("You have not applied to this ipo");
            }
            appliedIpoService.deleteByUserAndIpo(user, ipo);
            return ResponseEntity.ok("Unmarked Successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Please Login and try again");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Please try again later");
        }

    }

    @GetMapping("/applied-ipo")
    public ResponseEntity<?> findByUser(Principal principal) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);

            List<AppliedIpo> appliedIpos = appliedIpoService.findByUser(user);

            appliedIpos.sort((a, b) -> b.getIpo().getListingDate()
                    .compareTo(a.getIpo().getListingDate()));

            List<AppliedIpoResponseDTO> response = appliedIpos.stream().map(applied -> {
                AppliedIpoResponseDTO dto = new AppliedIpoResponseDTO();
                dto.setId(applied.getId());
                dto.setIpo(modelMapper.map(applied.getIpo(), IPOResponseDTO.class));
                dto.setAppliedLot(applied.getAppliedLot());
                dto.setAllotment(applied.getAllotment());
                return dto;
            }).toList();
            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Please login and try again");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong" + e.getMessage());
        }
    }

    @GetMapping("/applied-ipo/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id, Principal principal) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            AppliedIpo appliedIpo = appliedIpoService.findById(id);
            if (appliedIpo.getUser() != user) {
                throw new RuntimeException("user have not applied for this ipo: " + id);
            }

            AppliedIpoResponseDTO res = modelMapper.map(appliedIpo, AppliedIpoResponseDTO.class);

            return ResponseEntity.ok(res);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Please login and try again");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong" + e.getMessage());
        }
    }

    @PatchMapping("/applied-ipo/{id}")
    public ResponseEntity<?> updateById(@PathVariable UUID id, Principal principal,
            @RequestBody Map<String, Object> req) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            AppliedIpo appliedIpo = appliedIpoService.findById(id);
            if (!appliedIpo.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("User does not own this IPO: " + id);
            }

            for (String key : req.keySet()) {
                switch (key) {
                    case "lot" -> {
                        Integer lot = (Integer) req.get("lot");

                        if (lot == null || lot < 1) {
                            return ResponseEntity.badRequest().body("Invalid lot value");
                        }

                        if (!lot.equals(appliedIpo.getAppliedLot())) {
                            appliedIpo.setAppliedLot(lot);
                        }
                    }
                    case "allotment" -> {
                        String allotmentStr = req.get("allotment").toString();
                        AllotmentStatus status = AllotmentStatus.valueOf(allotmentStr);

                        if (appliedIpo.getAllotment() != status) {
                            if (status == AllotmentStatus.ALLOTED) {
                                AllotedIpo allotedIpo = allotedIpoService.create(appliedIpo.getId());
                                appliedIpo.setAllotedIpo(allotedIpo);
                                appliedIpo.setAllotment(status);
                            } else if (status == AllotmentStatus.NOT_ALLOTED) {
                                allotedIpoService.deleteAllotment(appliedIpo.getId());
                                appliedIpo.setAllotment(status);
                            }
                        }
                    }
                    default -> {
                        return ResponseEntity.badRequest()
                                .body("Invalid field: " + key);
                    }
                }
            }
            appliedIpoService.save(appliedIpo);
            AppliedIpoResponseDTO res = modelMapper.map(appliedIpo, AppliedIpoResponseDTO.class);
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
