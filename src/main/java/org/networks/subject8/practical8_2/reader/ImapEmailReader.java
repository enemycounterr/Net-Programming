package org.networks.subject8.practical8_2.reader;

import org.networks.subject8.practical8_2.config.ImapConfig;
import org.networks.subject8.practical8_2.model.EmailInfo;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ImapEmailReader implements EmailReader {
    private final ImapConfig config;
    private Store store;
    private Folder folder;
    private int lastCheckedMessageCount = 0;

    public ImapEmailReader(ImapConfig config) {
        this.config = config;
    }

    @Override
    public void connect() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", config.getHost());
        props.put("mail.imap.port", config.getPort());
        props.put("mail.imap.ssl.enable", config.isSslEnable());

        System.out.println("Connecting to " + config.getHost() + ":" + config.getPort() + "...");

        Session session = Session.getInstance(props);
        store = session.getStore("imaps");
        store.connect(config.getHost(), config.getUsername(), config.getPassword());

        System.out.println("Connected successfully!");

        System.out.println("Opening folder: " + config.getFolder());
        folder = store.getFolder(config.getFolder());
        folder.open(Folder.READ_ONLY);

        lastCheckedMessageCount = folder.getMessageCount();
        System.out.println("Current messages in folder: " + lastCheckedMessageCount);
    }


    @Override
    public List<EmailInfo> getNewEmails() throws MessagingException {
        List<EmailInfo> newEmails = new ArrayList<>();

        int currentMessageCount = folder.getMessageCount();

        if (currentMessageCount > lastCheckedMessageCount) {
            Message[] messages = folder.getMessages(lastCheckedMessageCount + 1, currentMessageCount);

            for (Message message : messages) {
                EmailInfo emailInfo = extractEmailInfo(message);
                newEmails.add(emailInfo);
            }

            lastCheckedMessageCount = currentMessageCount;
        }

        return newEmails;
    }

    @Override
    public void disconnect() throws MessagingException {
        if (folder != null && folder.isOpen()) {
            folder.close(false);
        }
        if (store != null && store.isConnected()) {
            store.close();
        }
        System.out.println("Disconnected from IMAP server.");
    }

    private EmailInfo extractEmailInfo(Message message) throws MessagingException {
        String from = "Unknown";
        if (message.getFrom() != null && message.getFrom().length > 0) {
            Address address = message.getFrom()[0];
            if (address instanceof InternetAddress internetAddress) {
                from = internetAddress.getPersonal() != null
                        ? internetAddress.getPersonal() + " <" + internetAddress.getAddress() + ">"
                        : internetAddress.getAddress();
            } else {
                from = address.toString();
            }
        }

        String subject = message.getSubject() != null ? message.getSubject() : "(No Subject)";

        LocalDateTime receivedDate = message.getReceivedDate() != null
                ? LocalDateTime.ofInstant(message.getReceivedDate().toInstant(), ZoneId.systemDefault())
                : LocalDateTime.now();

        String messageId = getMessageId(message);

        return new EmailInfo(from, subject, receivedDate, messageId);
    }

    private String getMessageId(Message message) throws MessagingException {
        String[] headers = message.getHeader("Message-ID");
        if (headers != null && headers.length > 0) {
            return headers[0];
        }
        return String.valueOf(message.getMessageNumber());
    }
}
