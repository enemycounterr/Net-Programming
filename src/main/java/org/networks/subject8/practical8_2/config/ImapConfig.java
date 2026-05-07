package org.networks.subject8.practical8_2.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImapConfig {
    private String host;
    private int port;
    private boolean sslEnable;
    private String username;
    private String password;
    private String folder;
    private int checkIntervalSeconds;
}
