package org.networks.subject5.individual5_1.multiThread;

import org.networks.subject5.individual5_1.singleThread.SingleThreadDownloader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadDownloader {
    public static void main(String[] args) {
        String fileUrl = "http://ab.kh.ua/books/Cay-Horstmann-Gary-Cornell-java2-7th-edition-book1-2007.pdf";
        String savePath = SingleThreadDownloader.getFileName(fileUrl);
        int threadCount = 4;

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            long fileSize = conn.getContentLengthLong();
            conn.disconnect();

            if (fileSize <= 0) {
                System.out.println("The server did not return the file size. Multi-threaded download is not possible");
                return;
            }

            long chunkSize = fileSize / threadCount;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            System.out.println("Size of the file: " + fileSize + " bytes. Running " + threadCount + " threads");

            for (int i = 0; i < threadCount; i++) {
                long start = i * chunkSize;
                long end = (i == threadCount - 1) ? fileSize - 1 : (start + chunkSize - 1);

                executor.execute(new DownloadTask(fileUrl, start, end, savePath));
            }

            executor.shutdown();
            if (executor.awaitTermination(1, TimeUnit.HOURS)) {
                System.out.println("File downloaded success!");
            } else {
                System.out.println("File wasn't downloaded");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
