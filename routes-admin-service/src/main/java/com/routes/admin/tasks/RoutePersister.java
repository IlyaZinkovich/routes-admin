package com.routes.admin.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routes.admin.api.Place;
import com.routes.admin.api.SaveRoutesTask;
import com.routes.admin.journal.JournalClient;
import com.routes.admin.journal.Update;
import com.routes.admin.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ.Socket;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static java.lang.Thread.currentThread;

@Component
public class RoutePersister {

    @Autowired
    @Qualifier("saveRoutesTaskSubscriber")
    private Socket subscriber;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RouteService routeService;

    @Autowired
    private JournalClient journalClient;

    @PostConstruct
    public void subscribe() {
        new Thread(this::processIncomingMessages).start();
    }

    private void processIncomingMessages() {
        while (!currentThread().isInterrupted()) {
            String taskType = subscriber.recvStr();
            String taskContents = subscriber.recvStr();
            SaveRoutesTask saveRoutesTask = convert(taskContents);
            Place toPlace = saveRoutesTask.getRoute().getToPlace();
            journalClient.save(new Update(toPlace.getCity(), toPlace.getCountry(),
                    saveRoutesTask.getRoute().getDate(), saveRoutesTask.getRoute().getSource()));
            routeService.saveRoute(saveRoutesTask.getRoute());
        }
    }

    private SaveRoutesTask convert(String contents) {
        try {
            return objectMapper.readValue(contents, SaveRoutesTask.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
