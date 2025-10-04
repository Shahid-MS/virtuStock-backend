// package com.virtu_stock.IPOAlerts;

// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/ipos")
// public class IPOAlertsController {

//     @Autowired
//     private IPOAlertsService ipoAlertsService;

//     @GetMapping
//     public Map<String, Object> getIPOs(
//             @RequestParam String status,
//             @RequestParam(required = false) String type,
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "1") int limit) {

//         return ipoAlertsService.getIPOs(status, type, page, limit);
//     }

//     @GetMapping("/{identifier}")
//     public Map<String, Object> getIPO(@PathVariable String identifier) {
//         return ipoAlertsService.getIPO(identifier);
//     }
// }
