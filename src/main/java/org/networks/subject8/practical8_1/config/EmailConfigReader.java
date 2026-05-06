package org.networks.subject8.practical8_1.config;

import java.io.IOException;

public interface EmailConfigReader {
    EmailConfig read() throws IOException;
}
