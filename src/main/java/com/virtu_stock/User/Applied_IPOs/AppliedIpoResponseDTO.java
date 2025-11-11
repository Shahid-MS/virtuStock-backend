package com.virtu_stock.User.Applied_IPOs;

import java.util.UUID;

import com.virtu_stock.Enum.AllotmentStatus;
import com.virtu_stock.IPO.IPOResponseDTO;

import lombok.Data;

@Data
public class AppliedIpoResponseDTO {
    private UUID id;
    private IPOResponseDTO ipo;
    private Integer appliedLot;
    private AllotmentStatus allotment;
}
