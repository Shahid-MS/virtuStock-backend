package com.virtu_stock.IPO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virtu_stock.Enum.IPOStatus;
import com.virtu_stock.Enum.Verdict;
import com.virtu_stock.GMP.GMP;
import com.virtu_stock.Subscription.Subscription;

@Service
public class IPOService {
    @Autowired
    private IPORepository ipoRepo;

    public List<IPO> fetchAllIpos() {
        List<IPO> ipos = ipoRepo.findAllByOrderByEndDateDesc();
        return ipos;
    }

    public List<IPO> fetchIPOByStatus(String status) {
        List<IPO> ipos = ipoRepo.findAll().stream()
                .filter(ipo -> ipo.getStatus() == IPOStatus.valueOf(status.toUpperCase()))
                .toList();
        return ipos;
    }

    public IPO getIpoById(UUID id) {
        return ipoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("IPO not found with id: " + id));
    }

    public IPO saveIpo(IPO ipo) {
        return ipoRepo.save(ipo);
    }

    public void updateSubscriptions(IPO existingIpo, List<Subscription> newSubs) {
        List<Subscription> existingSubs = existingIpo.getSubscriptions();
        for (Subscription subs : newSubs) {
            Optional<Subscription> foundSub = existingSubs.stream()
                    .filter(s -> s.getName().equalsIgnoreCase(subs.getName()))
                    .findFirst();
            if (foundSub.isPresent()) {
                if (foundSub.get().getSubsvalue() != subs.getSubsvalue()) {
                    foundSub.get().setSubsvalue(subs.getSubsvalue());
                }
            } else {
                existingSubs.add(
                        Subscription.builder()
                                .name(subs.getName())
                                .subsvalue(subs.getSubsvalue())
                                .build());
            }
        }
        existingIpo.setSubscriptions(existingSubs);
    }

    public void updateGmp(IPO existingIpo, List<GMP> newGmp) {
        List<GMP> existingGMP = existingIpo.getGmp();
        for (GMP g : newGmp) {
            Optional<GMP> foundGMP = existingGMP.stream().filter(s -> s.getGmpDate().equals(g.getGmpDate()))
                    .findFirst();
            if (foundGMP.isPresent()) {
                if (foundGMP.get().getGmp() != g.getGmp()) {
                    foundGMP.get().setGmp(g.getGmp());
                    foundGMP.get().setLastUpdated(LocalDateTime.now());
                }
            } else {
                existingGMP.add(GMP.builder().gmp(g.getGmp()).gmpDate(g.getGmpDate())
                        .lastUpdated(LocalDateTime.now()).build());
            }
        }
        existingIpo.setGmp(existingGMP);
    }

    public void updateVerdict(IPO existingIpo, Verdict newVerdict) {
        if (existingIpo.getVerdict() != newVerdict) {
            existingIpo.setVerdict(newVerdict);
        }
    }

    public void updateIssueSize(IPO existingIpo, IssueSize newIssueSize) {
        if (existingIpo.getIssueSize().getFresh() != newIssueSize.getFresh()) {
            existingIpo.getIssueSize().setFresh(newIssueSize.getFresh());
        }
        if (existingIpo.getIssueSize().getOfferForSale() != newIssueSize.getOfferForSale()) {
            existingIpo.getIssueSize().setOfferForSale(newIssueSize.getOfferForSale());
        }
        if (existingIpo.getIssueSize().getTotalIssueSize() != newIssueSize.getTotalIssueSize()) {
            existingIpo.getIssueSize().setTotalIssueSize(newIssueSize.getTotalIssueSize());
        }
    }

    public void deleteIpo(UUID id) {
        ipoRepo.deleteById(id);
    }

    public List<IPO> fetchIPOByListingPending() {
        List<IPO> ipos = ipoRepo.findByListingDateLessThanEqual(LocalDate.now());
        return ipos;
    }

    public List<Object[]> getIpoCountByMonthAndYear(Integer year) {
        return ipoRepo.countIpoByMonthAndYear(year);
    }
}
