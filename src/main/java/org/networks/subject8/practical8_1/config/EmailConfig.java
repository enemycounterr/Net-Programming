package org.networks.subject8.practical8_1.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailConfig {
    private String smtpHost;
    private int smtpPort;
    private boolean smtpAuth;
    private boolean smtpStartTls;

    private String username;
    private String password;

    private String from;
    private String subject;
    private String[] recipients;

    private String htmlTemplate;
    private String[] attachments;
}
