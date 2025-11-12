package com.virtu_stock.User.Alloted_IPOs;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtu_stock.User.Applied_IPOs.AppliedIpo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alloted_ipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AllotedIpo {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_ipo_id", nullable = false, unique = true)
    private AppliedIpo appliedIpo;

    @Column(nullable = false)
    private Integer allotedLot;

    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "tax_deducted")
    private Double taxDeducted;

    @Column(name = "alloted_date")
    private LocalDate allotedDate;

    @PrePersist
    public void initialze() {
        this.allotedDate = LocalDate.now();
    }

    @Transient
    public Double getNetReturn() {
        if (sellPrice == null || appliedIpo == null || appliedIpo.getIpo() == null) {
            return 0.0;
        }
        Double listedPrice = appliedIpo.getIpo().getListedPrice();
        return (sellPrice - listedPrice) * allotedLot - (taxDeducted != null ? taxDeducted : 0.0);
    }

}
