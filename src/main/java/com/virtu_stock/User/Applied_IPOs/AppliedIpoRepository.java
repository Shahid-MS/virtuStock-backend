package com.virtu_stock.User.Applied_IPOs;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virtu_stock.IPO.IPO;
import com.virtu_stock.User.User;

public interface AppliedIpoRepository extends JpaRepository<AppliedIpo, UUID> {
    boolean existsByUserAndIpo(User user, IPO ipo);

    List<AppliedIpo> findByUser(User user);

    void deleteByUserAndIpo(User user, IPO ipo);
}
