package com.virtu_stock.User.Applied_IPOs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.virtu_stock.IPO.IPO;
import com.virtu_stock.User.User;

public interface AppliedIpoRepository extends JpaRepository<AppliedIpo, UUID> {
    boolean existsByUserAndIpo(User user, IPO ipo);

    Optional<AppliedIpo> findByUserAndIpo(User user, IPO ipo);

    List<AppliedIpo> findByUser(User user);

    void deleteByUserAndIpo(User user, IPO ipo);

    @Query("""
                SELECT MONTH(a.ipo.startDate), COUNT(a)
                FROM AppliedIpo a
                WHERE a.user.id = :userId
                  AND YEAR(a.ipo.startDate) = :year
                GROUP BY MONTH(a.ipo.startDate)
                ORDER BY MONTH(a.ipo.startDate)
            """)
    List<Object[]> countAppliedByUserMonthYear(
            @Param("userId") UUID userId,
            @Param("year") int year);

    @Query("""
                SELECT MONTH(a.ipo.startDate), COUNT(a)
                FROM AppliedIpo a
                WHERE a.user.id = :userId
                  AND a.allotment = 'ALLOTED'
                  AND YEAR(a.ipo.startDate) = :year
                GROUP BY MONTH(a.ipo.startDate)
                ORDER BY MONTH(a.ipo.startDate)
            """)
    List<Object[]> countAllotedByUserMonthYear(
            @Param("userId") UUID userId,
            @Param("year") int year);

}
