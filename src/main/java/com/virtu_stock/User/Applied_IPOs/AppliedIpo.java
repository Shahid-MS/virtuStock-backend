package com.virtu_stock.User.Applied_IPOs;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.virtu_stock.Enum.AllotmentStatus;
import com.virtu_stock.IPO.IPO;
import com.virtu_stock.User.User;
import com.virtu_stock.User.Alloted_IPOs.AllotedIpo;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applied_ipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppliedIpo {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ipo_id", nullable = false)
    private IPO ipo;

    @Column(name = "applied_lot")
    private Integer appliedLot;

    @Enumerated(EnumType.STRING)
    private AllotmentStatus allotment;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @OneToOne(mappedBy = "appliedIpo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Nullable
    private AllotedIpo allotedIpo;

    @Transient
    public AllotmentStatus getAllotment() {
        LocalDate allotmentDate = ipo.getAllotmentDate();
        if (LocalDate.now().isAfter(allotmentDate)) {
            return this.allotment;
        } else if (LocalDate.now().isEqual(allotmentDate)) {
            return AllotmentStatus.ALLOTMENT;
        }
        return AllotmentStatus.ALLOTMENT_PENDING;
    }

    @PrePersist
    public void initalize() {
        this.appliedDate = LocalDate.now();
    }

}
