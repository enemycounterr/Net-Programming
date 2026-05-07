package org.networks.subject8.practical8_2;

import org.networks.subject8.practical8_2.config.ImapConfig;
import org.networks.subject8.practical8_2.config.ImapConfigReader;
import org.networks.subject8.practical8_2.config.PropertyImapConfigReader;
import org.networks.subject8.practical8_2.logger.ConsoleEmailLogger;
import org.networks.subject8.practical8_2.logger.EmailLogger;
import org.networks.subject8.practical8_2.monitor.EmailMonitor;
import org.networks.subject8.practical8_2.monitor.PeriodicEmailMonitor;
import org.networks.subject8.practical8_2.reader.EmailReader;
import org.networks.subject8.practical8_2.reader.ImapEmailReader;

public class EmailMonitorApp {
    public static void main(String[] args) {
        System.out.println("=== Email Monitor ===\n");

        try {
            ImapConfigReader configReader = new PropertyImapConfigReader();
            ImapConfig config = configReader.read();

            EmailReader reader = new ImapEmailReader(config);
            EmailLogger logger = new ConsoleEmailLogger();
            EmailMonitor monitor = new PeriodicEmailMonitor(
                    reader,
                    logger,
                    config.getCheckIntervalSeconds()
            );

            Runtime.getRuntime().addShutdownHook(new Thread(monitor::stop));

            monitor.start();

            System.out.println("Press Enter to stop monitoring...");
            System.in.read();

            monitor.stop();
            System.out.println("Email monitor stopped.");

        } catch (Exception e) {
            System.err.println("Failed to start email monitor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
