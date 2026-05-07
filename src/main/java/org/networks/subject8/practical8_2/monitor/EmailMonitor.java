package org.networks.subject8.practical8_2.monitor;

import javax.mail.MessagingException;

public interface EmailMonitor {
    void start() throws MessagingException;

    void stop();
}
