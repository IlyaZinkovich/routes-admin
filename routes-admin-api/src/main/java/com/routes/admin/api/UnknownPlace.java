package com.routes.admin.api;

public class UnknownPlace extends Place {

    public UnknownPlace(String city, String country, Double latitude, Double longitude) {
        super(city, country, latitude, longitude);
    }

    @Override
    public boolean known() {
        return false;
    }
}
