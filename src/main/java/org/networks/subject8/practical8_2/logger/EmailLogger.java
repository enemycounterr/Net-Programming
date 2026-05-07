package org.networks.subject8.practical8_2.logger;

import org.networks.subject8.practical8_2.model.EmailInfo;

import java.util.List;

public interface EmailLogger {
    void log(List<EmailInfo> emails);
}
