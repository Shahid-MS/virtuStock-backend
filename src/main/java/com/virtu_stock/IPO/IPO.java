package com.virtu_stock.IPO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.virtu_stock.Enum.IPOStatus;
import com.virtu_stock.Enum.Verdict;
import com.virtu_stock.GMP.GMP;
import com.virtu_stock.Subscription.Subscription;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    private Double minPrice;

    @Column(name = "listed_price")
    private Double listedPrice;

    @Column(name = "max_price")
    private Double maxPrice;

    @Column(name = "minimum_quantity")
    private Integer minQty;

    private String logo;
    @Column(name = "issue_size")
    private IssueSize issueSize;

    @Column(name = "prospectus_url")
    private String prospectusUrl;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Column(name = "allotment_date")
    private LocalDate allotmentDate;

    @Enumerated(EnumType.STRING)
    private Verdict verdict;

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
    @OrderBy("gmpDate Desc")
    private List<GMP> gmp;

    @PrePersist
    public void initializeDefaults() {
        // subscription
        if (subscriptions == null || subscriptions.isEmpty()) {
            subscriptions = new ArrayList<>();
            subscriptions.add(new Subscription("QIB", 0.00));
            subscriptions.add(new Subscription("Non-Institutional", 0.00));
            subscriptions.add(new Subscription("Retailer", 0.00));
            subscriptions.add(new Subscription("Total", 0.00));
        }
        // GMP
        if (gmp == null || gmp.isEmpty()) {
            gmp = new ArrayList<>();
            gmp.add(new GMP(0, LocalDate.now(), LocalDateTime.now()));
        }

        // Verdict
        if (verdict == null) {
            verdict = Verdict.NOT_REVIEWED;
        }

    }

    @Transient
    public IPOStatus getStatus() {
        LocalDate today = LocalDate.now();
        if (startDate == null || endDate == null) {
            return null;
        }

        if (today.isAfter(listingDate)) {
            return IPOStatus.CLOSED;
        }

        if (today.isEqual(listingDate)) {
            LocalTime nowTime = LocalTime.now();
            if (nowTime.isAfter(LocalTime.of(10, 0))) {
                return IPOStatus.LISTED;
            } else {
                return IPOStatus.LISTING_PENDING;
            }
        }

        if (today.isAfter(allotmentDate)) {
            return IPOStatus.LISTING_PENDING;
        }

        if (today.isEqual(allotmentDate)) {
            LocalTime nowTime = LocalTime.now();
            if (nowTime.isAfter(LocalTime.of(17, 0))) {
                return IPOStatus.LISTING_PENDING;
            } else {
                return IPOStatus.ALLOTMENT;
            }
        }

        if (today.isAfter(endDate)) {
            return IPOStatus.ALLOTMENT_PENDING;
        }

        if (today.isEqual(endDate)) {
            LocalTime nowTime = LocalTime.now();
            if (nowTime.isAfter(LocalTime.of(17, 0))) {
                return IPOStatus.LISTING_PENDING;
            } else {
                return IPOStatus.OPEN;
            }
        }

        if (today.isAfter(startDate)) {
            return IPOStatus.OPEN;
        }

        if (today.isEqual(startDate)) {
            LocalTime nowTime = LocalTime.now();
            if (nowTime.isAfter(LocalTime.of(9, 0))) {
                return IPOStatus.OPEN;
            }
        }
        return IPOStatus.UPCOMING;
    }

    @Transient
    @JsonProperty("listingReturn")
    public Double getListingReturn() {
        if (listedPrice == null) {
            return 0.0;
        }
        return listedPrice - maxPrice;
    }

    @Transient
    @JsonProperty("listingReturnPercent")
    public Double getListingReturnPercent() {
        Double listingReturn = getListingReturn();
        if (listingReturn == 0.0) {
            return 0.0;
        }
        Double listingReturnPercent = (listingReturn / maxPrice) * 100;
        return Math.round(listingReturnPercent * 100.0) / 100.0;
    }

}
