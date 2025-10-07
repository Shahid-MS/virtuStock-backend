package com.virtu_stock.IPO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IPOService {
    @Autowired
    private IPORepository ipoRepo;

    public List<IPO> fetchIpos() {
        List<IPO> ipos = ipoRepo.findAll();
        return ipos;
    }
}
