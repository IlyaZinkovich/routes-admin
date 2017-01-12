package com.routes.admin.service;

import com.routes.admin.api.FindRoutesTask;
import com.routes.admin.api.Place;
import com.routes.admin.api.Route;
import com.routes.admin.journal.JournalClient;
import com.routes.admin.journal.Update;
import com.routes.admin.tasks.TaskPublisher;
import com.routes.admin.repository.RoutesRepository;
import com.routes.geolocation.client.GoogleGeocodingClient;
import com.routes.geolocation.model.GeoObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.Collections.emptyList;

@Service
public class RouteService {

    @Autowired
    private RoutesRepository routesRepository;

    @Autowired
    private GoogleGeocodingClient geocodingClient;

    @Autowired
    private TaskPublisher taskPublisher;

    @Autowired
    private JournalClient journalClient;

    public List<Route> getRoutes(String city, String country, LocalDate startDate, LocalDate endDate) {
        Place destination = convert(geocodingClient.findGeoObject(city + ", " + country));
        return destination.known() ? getRoutesFromKnownDestination(destination, startDate, endDate)
                : emptyList();
    }

    public void searchRoutes(String city, String country, LocalDate startDate, LocalDate endDate) {
        Place destination = convert(geocodingClient.findGeoObject(city + ", " + country));
        int days = startDate.until(endDate).getDays();
        IntStream.range(0, days + 1)
                .mapToObj(startDate::plusDays)
                .map(date -> new Update(city, country, date, "Flickr"))
                .filter(update -> !journalClient.exists(update))
                .map(update -> new FindRoutesTask(destination, update.getDate()))
                .forEach(taskPublisher::publish);
    }

    private List<Route> getRoutesFromKnownDestination(Place destination, LocalDate startDate, LocalDate endDate) {
        return routesRepository.findRoutes(destination.getCity(), destination.getCountry(), startDate, endDate);
    }

    public void saveRoute(Route route) {
        routesRepository.save(route);
    }

    private Place convert(GeoObject geoObject) {
        return new Place(geoObject.getCity(), geoObject.getCountry(),
                geoObject.getLatitude(), geoObject.getLongitude());
    }
}
