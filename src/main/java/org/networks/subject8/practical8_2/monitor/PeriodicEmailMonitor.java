package org.networks.subject8.practical8_2.monitor;

import org.networks.subject8.practical8_2.logger.EmailLogger;
import org.networks.subject8.practical8_2.model.EmailInfo;
import org.networks.subject8.practical8_2.reader.EmailReader;

import javax.mail.MessagingException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicEmailMonitor implements EmailMonitor {
    private final EmailReader reader;
    private final EmailLogger logger;
    private final int intervalSeconds;
    private final ScheduledExecutorService scheduler;

    public PeriodicEmailMonitor(EmailReader reader, EmailLogger logger, int intervalSeconds) {
        this.reader = reader;
        this.logger = logger;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void start() throws MessagingException {
        reader.connect();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<EmailInfo> newEmails = reader.getNewEmails();

                if (!newEmails.isEmpty()) {
                    logger.log(newEmails);
                }
            } catch (Exception e) {
                System.err.println("Error checking emails: " + e.getMessage());
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);

        System.out.println("Email monitor started. Checking every " + intervalSeconds + " seconds...");
    }

    @Override
    public void stop() {
        System.out.println("\nStopping email monitor...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        try {
            reader.disconnect();
        } catch (MessagingException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
