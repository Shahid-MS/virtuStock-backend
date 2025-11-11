package com.virtu_stock.User.Alloted_IPOs;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AllotedIpoRepository extends JpaRepository<AllotedIpo, UUID> {
    public boolean existsByAppliedIpoId(UUID id);
}
