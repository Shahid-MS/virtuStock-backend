package com.virtu_stock.IPO;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IPOJpaRepository extends JpaRepository<IPO, UUID> {

}
