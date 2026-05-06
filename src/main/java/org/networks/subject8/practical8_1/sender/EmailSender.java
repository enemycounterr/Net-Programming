package org.networks.subject8.practical8_1.sender;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailSender {
    void send() throws MessagingException, IOException;
}
