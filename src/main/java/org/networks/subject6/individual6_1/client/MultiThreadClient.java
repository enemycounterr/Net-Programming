package org.networks.subject6.individual6_1.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadClient {
    private static final int PORT = 5556;
    private static final String HOST = "127.0.0.1";

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        AtomicInteger counter = new AtomicInteger(0);
        
        System.out.println("Launching 1000 sessions...");

        for (int i = 0; i < 1000; i++) {
            executor.submit(new ClientSession(HOST, PORT, counter));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("All sessions finished.");
    }

}
