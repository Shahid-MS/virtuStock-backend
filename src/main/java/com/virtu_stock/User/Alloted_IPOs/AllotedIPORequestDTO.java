package com.virtu_stock.User.Alloted_IPOs;

import com.virtu_stock.User.Applied_IPOs.AppliedIpo;

import lombok.Data;

@Data
public class AllotedIPORequestDTO {
    private AppliedIpo appliedIpo;
    private Integer allotedLot;
    private Double sellPrice;
    private Double taxDeducted;
}
