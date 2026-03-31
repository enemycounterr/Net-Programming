package org.networks.subject5.individual5_2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadTask extends SwingWorker<Void, Integer> {
    private final DownloadModel model;
    private final int rowIndex;
    private final DefaultTableModel tableModel;
    private volatile boolean paused = false;

    public DownloadTask(DownloadModel model, int rowIndex, DefaultTableModel tableModel) {
        this.model = model;
        this.rowIndex = rowIndex;
        this.tableModel = tableModel;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public DownloadModel getModel() {
        return model;
    }

    @Override
    protected Void doInBackground() throws Exception {
        URL url = new URL(model.getUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        long remoteSize = conn.getContentLengthLong();
        File file = new File(model.getFileName());
        long localSize = file.exists() ? file.length() : 0;

        if (localSize == remoteSize && remoteSize > 0) {
            updateStatus(DownloadStatus.COMPLETED, 100);
            return null;
        }

        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (localSize > 0) {
            conn.setRequestProperty("Range", "bytes=" + localSize + "-");
        }

        try (InputStream in = conn.getInputStream();
             RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

            if (remoteSize <= 0) {
                long remainingSize = conn.getContentLengthLong();
                if (remainingSize > 0) {
                    remoteSize = localSize + remainingSize;
                }
            }

            raf.seek(localSize);
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalDownloaded = localSize;

            while ((bytesRead = in.read(buffer)) != -1) {
                if (isCancelled()) return null;
                while (paused) {
                    Thread.sleep(100);
                }

                raf.write(buffer, 0, bytesRead);
                totalDownloaded += bytesRead;
                
                if (remoteSize > 0) {
                    int progress = (int) ((totalDownloaded * 100L) / remoteSize);
                    publish(Math.min(progress, 100));
                } else {
                    publish(-1);
                }
            }
        } catch (Exception e) {
            updateStatus(DownloadStatus.ERROR, model.getProgress());
            return null;
        }
        updateStatus(DownloadStatus.COMPLETED, 100);
        return null;
    }

    private void updateStatus(DownloadStatus status, int progress) {
        model.setStatus(status);
        model.setProgress(progress);
        publish(progress);
    }

    @Override
    protected void process(List<Integer> chunks) {
        int latestProgress = chunks.get(chunks.size() - 1);

        model.setProgress(latestProgress);

        if (latestProgress >= 0) {
            tableModel.setValueAt(latestProgress + "%", rowIndex, 2);
        } else {
            tableModel.setValueAt("Unknown", rowIndex, 2);
        }

        tableModel.setValueAt(model.getStatus().toString(), rowIndex, 3);
    }


}
