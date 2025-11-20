package com.virtu_stock.User.Alloted_IPOs;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.virtu_stock.User.Applied_IPOs.AppliedIpo;
import com.virtu_stock.User.Applied_IPOs.AppliedIpoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AllotedIpoService {
    private final AllotedIpoRepository allotedIpoRepository;
    private final AppliedIpoService appliedIpoService;

    public AllotedIpo create(UUID appliedIpoId) {
        boolean alreadyAlloted = existsByAppliedIpoId(appliedIpoId);
        if (alreadyAlloted) {
            throw new RuntimeException("Ipo already Alloted");
        }
        AppliedIpo appliedIpo = appliedIpoService.findById(appliedIpoId);
        AllotedIpo allotedIpo = new AllotedIpo();
        allotedIpo.setAppliedIpo(appliedIpo);
        allotedIpo.setAllotedLot(1);
        allotedIpo.setSellPrice(null);
        allotedIpo.setTaxDeducted(0.0);
        return allotedIpoRepository.save(allotedIpo);
    }

    public AllotedIpo save(AllotedIpo allotedIpo) {
        return allotedIpoRepository.save(allotedIpo);
    }

    public boolean existsByAppliedIpoId(UUID appliedIpoId) {
        return allotedIpoRepository.existsByAppliedIpoId(appliedIpoId);
    }

    public AllotedIpo findById(UUID id) {
        return allotedIpoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Alloted Ipo find with : " + id));
    }

    public List<Object[]> sumMonthlyProfit(UUID id, int year) {
        return allotedIpoRepository.sumMonthlyProfit(id, year);
    }

    @Transactional
    public void deleteAllotment(UUID appliedIpoId) {
        boolean alloted = existsByAppliedIpoId(appliedIpoId);
        if (!alloted) {
            throw new RuntimeException("Ipo Not Alloted");
        }
        AppliedIpo appliedIpo = appliedIpoService.findById(appliedIpoId);
        appliedIpo.setAllotedIpo(null);
        allotedIpoRepository.deleteByAppliedIpo(appliedIpo);
    }
}
