package org.networks.subject8.practical8_1.sender;

import org.networks.subject8.practical8_1.config.EmailConfig;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

public class HtmlEmailSender implements EmailSender {
    private final EmailConfig config;
    private final Session session;
    private final MimeMessage message;

    public HtmlEmailSender(EmailConfig config) {
        this.config = config;
        this.session = createSession();
        this.message = new MimeMessage(session);
    }

    @Override
    public void send() throws MessagingException, IOException {
        System.out.println("=== Preparing Email ===");

        message.setFrom(new InternetAddress(config.getUsername(), config.getFrom()));
        message.setRecipients(Message.RecipientType.TO, parseRecipients());
        message.setSubject(config.getSubject());

        System.out.println("From: " + config.getFrom() + " <" + config.getUsername() + ">");
        System.out.println("To: " + String.join(", ", config.getRecipients()));
        System.out.println("Subject: " + config.getSubject());

        MimeMultipart multipart = new MimeMultipart();


        MimeBodyPart htmlPart = new MimeBodyPart();
        String html = loadTemplate(config.getHtmlTemplate());
        htmlPart.setContent(html, "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlPart);

        System.out.println("Template: " + config.getHtmlTemplate());

        if (config.getAttachments() != null && config.getAttachments().length > 0) {
            System.out.println("Attachments:");
            for (String attachmentPath : config.getAttachments()) {
                addAttachment(multipart, attachmentPath);
            }
        } else {
            System.out.println("No attachments");
        }

        message.setContent(multipart);


        System.out.println("\n=== Sending Email ===");
        Transport.send(message);

        System.out.println("Email sent successfully!");
    }


    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getSmtpHost());
        props.put("mail.smtp.port", config.getSmtpPort());
        props.put("mail.smtp.auth", config.isSmtpAuth());
        props.put("mail.smtp.starttls.enable", config.isSmtpStartTls());


        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        };

        return Session.getInstance(props, authenticator);
    }


    private InternetAddress[] parseRecipients() throws MessagingException {
        InternetAddress[] addresses = new InternetAddress[config.getRecipients().length];
        for (int i = 0; i < config.getRecipients().length; i++) {
            addresses[i] = new InternetAddress(config.getRecipients()[i]);
        }
        return addresses;
    }


    private String loadTemplate(String templateName) throws IOException {
        String templatePath = "/templates/" + templateName;

        try (InputStream is = HtmlEmailSender.class.getResourceAsStream(templatePath)) {
            if (is == null) {
                throw new IOException("Template not found: " + templatePath);
            }

            return new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }


    private void addAttachment(MimeMultipart multipart, String attachmentPath) throws MessagingException {
        File file = new File(attachmentPath);

        if (!file.exists()) {
            System.err.println(" File not found: " + attachmentPath);
            return;
        }

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new DataHandler(new FileDataSource(file)));
        attachmentPart.setFileName(file.getName());
        multipart.addBodyPart(attachmentPart);

        System.out.println(file.getName() + " (" + formatFileSize(file.length()) + ")");
    }


    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
