package com.routes.admin.journal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class JournalClient {

    @Autowired
    private Jedis jedis;

    public void save(Update update) {
        jedis.setnx(update.toString(), "incoming");
    }

    public boolean exists(Update update) {
        return jedis.exists(update.toString());
    }
}
