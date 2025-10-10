package com.virtu_stock.IPO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virtu_stock.GMP.GMP;
import com.virtu_stock.Subscription.Subscription;

@Service
public class IPOService {
    @Autowired
    private IPORepository ipoRepo;

    public List<IPO> fetchAllIpos() {
        List<IPO> ipos = ipoRepo.findAll();
        return ipos;
    }

    public IPO fetchIpo(UUID id) {
        return ipoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("IPO not found with id: " + id));
    }

    public IPO updateIpo(UUID id, IPO ipo) {
        IPO existingIpo = ipoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("IPO not found with id: " + id));

        // Subscription Update
        List<Subscription> latestSubs = ipo.getSubscriptions();
        if (latestSubs != null) {
            List<Subscription> existingSubs = existingIpo.getSubscriptions();
            for (Subscription subs : latestSubs) {
                Optional<Subscription> foundSub = existingSubs.stream()
                        .filter(s -> s.getName().equalsIgnoreCase(subs.getName()))
                        .findFirst();
                if (foundSub.isPresent()) {
                    foundSub.get().setSubsvalue(subs.getSubsvalue());
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

        // Update GMP
        List<GMP> latestGMP = ipo.getGmp();
        if (latestGMP != null) {
            List<GMP> existingGMP = existingIpo.getGmp();
            if (existingGMP == null) {
                existingGMP = new ArrayList<>();
            }

            for (GMP g : latestGMP) {
                Optional<GMP> foundGMP = existingGMP.stream().filter(s -> s.getGmpDate().equals(g.getGmpDate()))
                        .findFirst();
                System.out.println(foundGMP);
                if (foundGMP.isPresent()) {
                    foundGMP.get().setGmp(g.getGmp());
                    foundGMP.get().setLastUpdated(LocalDateTime.now());
                } else {
                    existingGMP.add(GMP.builder().gmp(g.getGmp()).gmpDate(g.getGmpDate())
                            .lastUpdated(LocalDateTime.now()).build());
                }
            }
            existingIpo.setGmp(existingGMP);
        }

        ipoRepo.save(existingIpo);
        return existingIpo;
    }
}
