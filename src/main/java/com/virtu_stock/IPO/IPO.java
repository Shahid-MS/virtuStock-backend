package com.virtu_stock.IPO;

import java.util.List;
import java.util.UUID;

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
@Table(name = "ipo") // Optional: specify table name
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPO {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String symbol;
    private String type;
    private String status;
    private String slug;
    private String infoUrl;
    private String nseInfoUrl;
    private String startDate;
    private String endDate;
    private String listingDate;
    private String priceRange;
    private Integer minQty;
    private Integer minAmount;
    private String logo;
    private String issueSize;
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

}
