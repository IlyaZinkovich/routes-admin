package com.routes.admin.service;

import com.routes.admin.api.FindRoutesTask;
import com.routes.admin.api.Place;
import com.routes.admin.api.Route;
import com.routes.admin.publisher.TaskPublisher;
import com.routes.admin.repository.RoutesRepository;
import com.routes.geolocation.client.GoogleGeocodingClient;
import com.routes.geolocation.model.GeoObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class RouteService {

    @Autowired
    private RoutesRepository routesRepository;

    @Autowired
    private GoogleGeocodingClient geocodingClient;

    @Autowired
    private TaskPublisher taskPublisher;

    public List<Route> getRoutes(String city, String country, LocalDate startDate, LocalDate endDate) {
        Place destination = convert(geocodingClient.findGeoObject(city + ", " + country));
        return destination.known() ? getRoutesFromKnownDestination(destination, startDate, endDate)
                : emptyList();
    }

    private List<Route> getRoutesFromKnownDestination(Place destination, LocalDate startDate, LocalDate endDate) {
        List<Route> routes =
                routesRepository.findRoutes(destination.getCity(), destination.getCountry(), startDate, endDate);
        if (routes.isEmpty()) {
            taskPublisher.publish(new FindRoutesTask(destination, startDate, endDate));
        }
        return routes;
    }

    public void saveRoute(Route route) {
        routesRepository.save(route);
    }

    private Place convert(GeoObject geoObject) {
        return new Place(geoObject.getCity(), geoObject.getCountry(),
                geoObject.getLatitude(), geoObject.getLongitude());
    }
}
