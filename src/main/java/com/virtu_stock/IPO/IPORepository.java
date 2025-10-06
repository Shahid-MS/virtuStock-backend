package com.virtu_stock.IPO;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IPORepository extends JpaRepository<IPO, UUID> {
    public boolean existsByIpoAlertId(String ipoAlertId);
}
