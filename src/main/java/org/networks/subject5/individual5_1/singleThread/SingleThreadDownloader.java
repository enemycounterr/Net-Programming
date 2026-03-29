package org.networks.subject5.individual5_1.singleThread;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SingleThreadDownloader {

    public static String getFileName(String url) {
        try {
            URL source = new URL(url);
            String query = source.getQuery();

            if (query != null && query.contains("filename=")) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    if (pair.startsWith("filename=")) {
                        String encodedName = pair.split("=")[1];
                        return URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
                    }
                }
            }

            String path = source.getPath();
            return path.substring(path.lastIndexOf('/') + 1);

        } catch (Exception e) {
            return "downloaded_file.bin";
        }
    }

    public static void main(String[] args) {
        String fileUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
        String savePath = getFileName(fileUrl);

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                int fileSize = httpConn.getContentLength();
                System.out.println("Found file. Size: " + fileSize + " byte");

                try (InputStream inputStream = httpConn.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(savePath)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalBytesRead = 0;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    System.out.println("Download complete: " + savePath);
                }
            } else {
                System.out.println("Error: Server response code " + responseCode);
            }

            httpConn.disconnect();

        } catch (IOException e) {
            System.err.println("The error occurred while downloading");
            e.printStackTrace();
        }
    }
}
