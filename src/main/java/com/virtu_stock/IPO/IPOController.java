package com.virtu_stock.IPO;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.virtu_stock.Configurations.AppConstants;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/ipo")
public class IPOController {
    @Autowired
    private IPOService ipoService;

    @GetMapping
    public IPOPageResponseDTO findAll(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        // Integer.parseInt(AppConstants.PAGE_SIZE)
        return ipoService.findAll(page, size, sortBy, sortDir);
    }

    @GetMapping(params = "status")
    public List<IPO> fetchIPOByStatus(@RequestParam String status) {
        return ipoService.fetchIPOByStatus(status);
    }

    @GetMapping("/{id}")
    public IPO fetchIpo(@PathVariable UUID id) {
        return ipoService.getIpoById(id);
    }

}
