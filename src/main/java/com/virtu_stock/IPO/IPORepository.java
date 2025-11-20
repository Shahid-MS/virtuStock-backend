package com.virtu_stock.IPO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IPORepository extends JpaRepository<IPO, UUID> {
    public boolean existsByIpoAlertId(String ipoAlertId);

    public List<IPO> findAllByOrderByEndDateDesc();

    public List<IPO> findByListingDateLessThanEqual(LocalDate date);

    @Query("""
                SELECT MONTH(i.startDate), COUNT(i)
                FROM IPO i
                WHERE YEAR(i.startDate) = :year
                GROUP BY MONTH(i.startDate)
            """)
    List<Object[]> countIpoByMonthAndYear(@Param("year") int year);
}
