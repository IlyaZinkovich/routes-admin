package com.routes.admin;

import com.routes.admin.journal.JournalConfig;
import com.routes.admin.repository.RepositoryConfig;
import com.routes.admin.tasks.MessagingConfig;
import com.routes.admin.web.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = {WebConfig.class, MessagingConfig.class, RepositoryConfig.class, JournalConfig.class})
public class RoutesAdminApp {

    public static void main(String[] args) {
        SpringApplication.run(RoutesAdminApp.class);
    }
}
