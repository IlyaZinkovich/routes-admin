package com.routes.admin.api;

import java.time.LocalDate;

public class FindRoutesTask {

    private Place destination;
    private LocalDate startDate;
    private LocalDate endDate;

    public FindRoutesTask(Place destination, LocalDate startDate, LocalDate endDate) {
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Place getDestination() {
        return destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
