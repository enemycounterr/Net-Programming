package org.networks.subject8.practical8_2.reader;

import org.networks.subject8.practical8_2.model.EmailInfo;

import javax.mail.MessagingException;
import java.util.List;

public interface EmailReader {
    void connect() throws MessagingException;

    List<EmailInfo> getNewEmails() throws MessagingException;

    void disconnect() throws MessagingException;
}
