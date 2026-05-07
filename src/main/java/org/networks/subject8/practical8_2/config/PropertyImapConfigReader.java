package org.networks.subject8.practical8_2.config;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyImapConfigReader implements ImapConfigReader {
    private static final Console CONSOLE = System.console();

    @Override
    public ImapConfig read() throws IOException {
        Properties props = loadProperties("/imap.properties");

        return ImapConfig.builder()
                .host(props.getProperty("mail.imap.host"))
                .port(Integer.parseInt(props.getProperty("mail.imap.port")))
                .sslEnable(Boolean.parseBoolean(props.getProperty("mail.imap.ssl.enable")))
                .username(props.getProperty("imap.username"))
                .password(readPassword(props))
                .folder(props.getProperty("imap.folder", "INBOX"))
                .checkIntervalSeconds(Integer.parseInt(props.getProperty("imap.check.interval.seconds", "30")))
                .build();
    }

    private Properties loadProperties(String resourcePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream is = PropertyImapConfigReader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            properties.load(is);
        }

        return properties;
    }

    private String readPassword(Properties properties) {
        String password = properties.getProperty("imap.password");
        System.out.println(password);


        if (password == null || password.trim().isEmpty() || "<secret>".equals(password)) {
            if (CONSOLE != null) {
                CONSOLE.printf("Enter IMAP password: ");
                return new String(CONSOLE.readPassword());
            } else {
                throw new IllegalArgumentException("Password not found and console is not available");
            }
        }

        return password;
    }
}
