package com.virtu_stock.User.Monthly_Summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummaryIPODTO {
    private String month;
    private int alloted;
    private int applied;
    private int total;
}
