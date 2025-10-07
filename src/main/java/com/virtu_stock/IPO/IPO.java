package com.virtu_stock.IPO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.virtu_stock.GMP.GMP;
import com.virtu_stock.Subscription.Subscription;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPO {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ipo_alert_id")
    private String ipoAlertId;

    private String name;
    private String symbol;
    private String type;
    private String status;
    @Column(name = "info_url")
    private String infoUrl;
    @Column(name = "nse_info_url")
    private String nseInfoUrl;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "listing_date")
    private LocalDate listingDate;
    @Column(name = "min_price")
    private Integer minPrice;

    @Column(name = "max_price")
    private Integer maxPrice;

    @Column(name = "minimum_quantity")
    private Integer minQty;

    private String logo;
    @Column(name = "issue_size")
    private String issueSize;
    @Column(name = "prospectus_url")
    private String prospectusUrl;

    @Column(columnDefinition = "TEXT")
    private String about;

    @ElementCollection
    @CollectionTable(name = "ipo_strengths", joinColumns = @JoinColumn(name = "ipo_id"))
    @Column(name = "strength")
    private List<String> strengths;

    @ElementCollection
    @CollectionTable(name = "ipo_risks", joinColumns = @JoinColumn(name = "ipo_id"))
    @Column(name = "risk")
    private List<String> risks;

    @ElementCollection
    @CollectionTable(name = "subscription", joinColumns = @JoinColumn(name = "ipo_id"))
    private List<Subscription> subscriptions;

    @ElementCollection
    @CollectionTable(name = "gmp", joinColumns = @JoinColumn(name = "ipo_id"))
    private List<GMP> gmp;

}
