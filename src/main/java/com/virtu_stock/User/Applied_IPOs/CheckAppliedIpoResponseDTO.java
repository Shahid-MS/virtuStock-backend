package com.virtu_stock.User.Applied_IPOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAppliedIpoResponseDTO {
    private boolean applied;
    private UUID appliedIpoId;
}
