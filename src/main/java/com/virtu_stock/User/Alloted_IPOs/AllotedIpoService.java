package com.virtu_stock.User.Alloted_IPOs;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AllotedIpoService {
    private final AllotedIpoRepository allotedIpoRepository;

    public AllotedIpo save(AllotedIpo allotedIpo) {
        return allotedIpoRepository.save(allotedIpo);
    }

    public boolean existsByAppliedIpoId(UUID appliedIpoId) {
        return allotedIpoRepository.existsByAppliedIpoId(appliedIpoId);
    }
}
