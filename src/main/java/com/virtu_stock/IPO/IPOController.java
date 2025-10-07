package com.virtu_stock.IPO;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/ipo")
public class IPOController {
    @Autowired
    private IPOService ipoService;

    @GetMapping
    public List<IPO> fetchIPOs() {
        return ipoService.fetchIpos();
    }
    

}
