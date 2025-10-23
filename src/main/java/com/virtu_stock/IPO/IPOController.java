package com.virtu_stock.IPO;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/ipo")
public class IPOController {
    @Autowired
    private IPOService ipoService;

    @GetMapping
    public List<IPO> fetchAllIPO() {
        return ipoService.fetchAllIpos();
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
