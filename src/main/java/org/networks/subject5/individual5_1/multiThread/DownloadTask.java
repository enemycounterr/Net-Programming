package org.networks.subject5.individual5_1.multiThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask implements Runnable {
    private final String fileUrl;
    private final long startByte;
    private final long endByte;
    private final String savePath;

    public DownloadTask(String fileUrl, long startByte, long endByte, String savePath) {
        this.fileUrl = fileUrl;
        this.startByte = startByte;
        this.endByte = endByte;
        this.savePath = savePath;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String byteRange = "bytes=" + startByte + "-" + endByte;
            conn.setRequestProperty("Range", byteRange);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL || conn.getResponseCode() == 200) {
                try (InputStream in = conn.getInputStream();
                     RandomAccessFile raf = new RandomAccessFile(savePath, "rw")) {

                    raf.seek(startByte);

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        raf.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Part [" + startByte + "-" + endByte + "] downloaded");
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
