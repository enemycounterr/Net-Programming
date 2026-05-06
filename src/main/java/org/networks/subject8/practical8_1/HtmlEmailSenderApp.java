package org.networks.subject8.practical8_1;

import org.networks.subject8.practical8_1.config.EmailConfig;
import org.networks.subject8.practical8_1.config.EmailConfigReader;
import org.networks.subject8.practical8_1.config.PropertyEmailConfigReader;
import org.networks.subject8.practical8_1.sender.EmailSender;
import org.networks.subject8.practical8_1.sender.HtmlEmailSender;

public class HtmlEmailSenderApp {
    public static void main(String[] args) {
        try {
            System.out.println("=== HTML Email Sender ===\n");

            EmailConfigReader configReader = new PropertyEmailConfigReader();
            EmailConfig config = configReader.read();

            EmailSender sender = new HtmlEmailSender(config);

            sender.send();

        } catch (Exception e) {
            System.err.println("\nFailed to send email: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }
}
