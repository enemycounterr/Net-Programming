package org.networks.subject8.practical8_2.config;

import java.io.IOException;

public interface ImapConfigReader {
    ImapConfig read() throws IOException;
}
