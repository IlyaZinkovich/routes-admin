package com.routes.admin.repository;

import com.routes.admin.api.Place;
import com.routes.admin.api.Route;
import com.routes.admin.model.PlaceNode;
import com.routes.admin.model.RouteRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;

@Repository
public class RoutesRepository {

    @Autowired
    private Neo4jOperations neo4jTemplate;

    private Map<PlaceNode, PlaceNode> existingPlaces;

    public List<Route> findRoutes(String city, String country, LocalDate after, LocalDate before) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("city", city);
        parameters.put("country", country);
        parameters.put("after", after.format(ofPattern("yyyy-MM-dd")));
        parameters.put("before", before.format(ofPattern("yyyy-MM-dd")));
        Iterable<RouteRelationship> routes = neo4jTemplate.queryForObjects(RouteRelationship.class,
                "MATCH (from)-[r:ROUTE]->(to {city: {city}, country: {country}}) " +
                        "WHERE r.date >= {after} AND r.date <= {before} RETURN r", parameters);
        return StreamSupport.stream(routes.spliterator(), false).map(this::convert).collect(toList());
    }

    private Route convert(RouteRelationship routeRelationship) {
        PlaceNode from = routeRelationship.getFrom();
        PlaceNode to = routeRelationship.getTo();
        Place fromPlace = new Place(from.getCity(), from.getCountry(), from.getLatitude(), from.getLongitude());
        Place toPlace = new Place(to.getCity(), to.getCountry(), to.getLatitude(), to.getLongitude());
        Route route = new Route(fromPlace, toPlace,
                parse(routeRelationship.getDate()), routeRelationship.getSource());
        route.setId(routeRelationship.getRelationshipId());
        return route;
    }

    private PlaceNode findPlaceIfExists(PlaceNode placeNode) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("city", placeNode.getCity());
        parameters.put("country", placeNode.getCountry());
        Iterable<PlaceNode> places = neo4jTemplate.queryForObjects(PlaceNode.class,
                "MATCH (from {city: {city}, country:{country}}) RETURN from", parameters);
        if (places.iterator().hasNext()) {
            return places.iterator().next();
        }
        return null;
    }

    public void save(Route route) {
        RouteRelationship routeRelationship = convert(route);
        PlaceNode from = findPlaceIfExists(routeRelationship.getFrom());
        if (from != null) routeRelationship.setFrom(from);
        PlaceNode to = findPlaceIfExists(routeRelationship.getTo());
        if (to != null) routeRelationship.setTo(findPlaceIfExists(to));
        neo4jTemplate.save(routeRelationship);
        route.setId(routeRelationship.getRelationshipId());
    }

    private RouteRelationship convert(Route route) {
        Place fromPlace = route.getFromPlace();
        PlaceNode fromPlaceNode = new PlaceNode(fromPlace.getCity(), fromPlace.getCountry(),
                fromPlace.getLatitude(), fromPlace.getLongitude());
        Place toPlace = route.getToPlace();
        PlaceNode toPlaceNode = new PlaceNode(toPlace.getCity(), toPlace.getCountry(),
                toPlace.getLatitude(), toPlace.getLongitude());
        return new RouteRelationship(fromPlaceNode, toPlaceNode,
                        route.getDate().format(ofPattern("yyyy-MM-dd")), route.getSource());
    }
}