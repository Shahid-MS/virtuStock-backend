package com.virtu_stock.User.Alloted_IPOs;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virtu_stock.User.Applied_IPOs.AppliedIpo;

public interface AllotedIpoRepository extends JpaRepository<AllotedIpo, UUID> {
    public boolean existsByAppliedIpoId(UUID id);

    public void deleteByAppliedIpo(AppliedIpo appliedIpo);
}
