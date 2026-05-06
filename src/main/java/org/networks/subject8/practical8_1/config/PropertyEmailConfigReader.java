package org.networks.subject8.practical8_1.config;

import org.apache.commons.lang3.StringUtils;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class PropertyEmailConfigReader implements EmailConfigReader {
    private static final Console CONSOLE = System.console();

    @Override
    public EmailConfig read() throws IOException {
        Properties smtpProps = loadProperties("/smtp.properties");

        Properties emailProps = loadProperties("/email.properties");

        return EmailConfig.builder()
                .smtpHost(smtpProps.getProperty("mail.smtp.host"))
                .smtpPort(Integer.parseInt(smtpProps.getProperty("mail.smtp.port")))
                .smtpAuth(Boolean.parseBoolean(smtpProps.getProperty("mail.smtp.auth")))
                .smtpStartTls(Boolean.parseBoolean(smtpProps.getProperty("mail.smtp.starttls.enable")))
                .username(emailProps.getProperty("email.username"))
                .password(readPassword(emailProps))
                .from(emailProps.getProperty("email.from"))
                .subject(emailProps.getProperty("email.subject"))
                .recipients(parseRecipients(emailProps.getProperty("email.recipients")))
                .htmlTemplate(emailProps.getProperty("email.template"))
                .attachments(parseAttachments(emailProps.getProperty("email.attachments")))
                .build();

    }

    private Properties loadProperties(String resourcePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream is = PropertyEmailConfigReader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            properties.load(is);
        }

        return properties;
    }

    private String readPassword(Properties properties) {
        String password = properties.getProperty("email.password");

        if (password == null || password.trim().isEmpty() || "<secret>".equals(password)) {
            if (CONSOLE != null) {
                CONSOLE.printf("Enter email password: ");
                return new String(CONSOLE.readPassword());
            } else {
                throw new IllegalArgumentException("Password not found and console is not available");
            }
        }

        return password;
    }


    private String[] parseRecipients(String source) {
        if (StringUtils.isEmpty(source)) {
            throw new IllegalArgumentException("Recipients list is empty");
        }

        return Arrays.stream(source.split("[,;\\s]+"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);
    }


    private String[] parseAttachments(String source) {
        if (StringUtils.isEmpty(source)) {
            return new String[0];
        }

        return Arrays.stream(source.split("[,;]+"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);
    }
}
