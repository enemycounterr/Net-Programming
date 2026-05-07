package org.networks.subject8.practical8_2.logger;

import org.networks.subject8.practical8_2.model.EmailInfo;

import java.util.List;

public class ConsoleEmailLogger implements EmailLogger {
    @Override
    public void log(List<EmailInfo> emails) {
        System.out.println("\n=== New Emails (" + emails.size() + ") ===");

        for (EmailInfo email : emails) {
            System.out.println(email);
        }

        System.out.println("========================\n");
    }
}
