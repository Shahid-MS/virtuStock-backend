package com.virtu_stock.IPO;

import java.util.List;

import lombok.Data;

@Data
public class IPOPageResponseDTO {
    private List<IPOResponseDTO> content;
    private Integer pageNumber;
    // private Integer pageSize;
    private Integer totalPageElements;
    private Integer totalPages;
    private Long totalElements;
    private boolean lastPage;
}
