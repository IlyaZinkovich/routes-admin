package com.routes.admin.api;

import java.time.LocalDate;

public class FindRoutesTask {

    private Place destination;
    private LocalDate date;

    public FindRoutesTask() {
    }

    public FindRoutesTask(Place destination, LocalDate date) {
        this.destination = destination;
        this.date = date;
    }

    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
