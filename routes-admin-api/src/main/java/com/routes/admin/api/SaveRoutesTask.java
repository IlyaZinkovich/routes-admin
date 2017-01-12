package com.routes.admin.api;

public class SaveRoutesTask {

    private Route route;

    public SaveRoutesTask() {
    }

    public SaveRoutesTask(Route route) {
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
