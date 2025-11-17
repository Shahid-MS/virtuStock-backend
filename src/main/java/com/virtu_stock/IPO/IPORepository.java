package com.virtu_stock.IPO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IPORepository extends JpaRepository<IPO, UUID> {
    public boolean existsByIpoAlertId(String ipoAlertId);

    public List<IPO> findAllByOrderByEndDateDesc();

    public List<IPO> findByListingDateLessThanEqual(LocalDate date);

    @Query("SELECT MONTH(i.startDate), COUNT(i) FROM IPO i GROUP BY MONTH(i.startDate)")
    List<Object[]> countIpoByMonth();
}
