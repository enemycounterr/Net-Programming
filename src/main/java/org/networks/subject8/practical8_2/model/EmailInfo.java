package org.networks.subject8.practical8_2.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record EmailInfo(
        String from,
        String subject,
        LocalDateTime receivedDate,
        String messageId
) implements Serializable {

    @Override
    public String toString() {
        return String.format("[%s] From: %s | Subject: %s",
                receivedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                from,
                subject
        );
    }
}
