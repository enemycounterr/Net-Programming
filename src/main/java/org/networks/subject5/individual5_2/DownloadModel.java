package org.networks.subject5.individual5_2;

import lombok.Data;

@Data
public class DownloadModel {
    private String url;
    private String fileName;
    private long size;
    private int progress;
    private DownloadStatus status;

    public DownloadModel(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
        this.progress = 0;
        this.status = DownloadStatus.DOWNLOADING;
    }

}
