package com.virtu_stock.User.Alloted_IPOs;

import com.virtu_stock.User.Applied_IPOs.AppliedIpoResponseDTO;

import lombok.Data;

@Data
public class AllotedIPOResponseDTO {
    private AppliedIpoResponseDTO appliedIpo;
    private Integer allotedLot;
    private Double sellPrice;
    private Double taxDeducted;
}
