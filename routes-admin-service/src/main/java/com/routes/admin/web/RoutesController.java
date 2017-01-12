package com.routes.admin.web;

import com.routes.admin.api.Route;
import com.routes.admin.model.RouteRelationship;
import com.routes.admin.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class RoutesController {

    @Autowired
    private RouteService routeService;

    @GetMapping(path = "/routes")
    public List<Route> getRoutes(@RequestParam("city") String city,
                                 @RequestParam("country") String country,
                                 @RequestParam("startDate")
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                 @RequestParam("endDate")
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return routeService.getRoutes(city, country, startDate, endDate);
    }

    @PostMapping(path = "/routes")
    public Long getRoutes(@RequestBody Route route) {
        routeService.saveRoute(route);
        return route.getId();
    }
}
