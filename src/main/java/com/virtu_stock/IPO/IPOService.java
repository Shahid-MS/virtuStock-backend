package com.virtu_stock.IPO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String updateIpo(UUID id, IPO ipo) {
        IPO existingIpo = ipoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("IPO not found with id: " + id));

        // Subscription Update
        List<Subscription> existingSubs = existingIpo.getSubscriptions();
        if (existingSubs == null) {
            existingSubs = new ArrayList<>();
        }

        List<Subscription> latestSubs = ipo.getSubscriptions();

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
        ipoRepo.save(existingIpo);
        return "Updated successfully";
    }
}
