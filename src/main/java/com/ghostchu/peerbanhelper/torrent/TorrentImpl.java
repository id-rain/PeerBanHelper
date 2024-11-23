package com.ghostchu.peerbanhelper.torrent;

import lombok.Setter;

@Setter
public class TorrentImpl implements Torrent {
    private boolean privateTorrent;
    private double progress;
    private long rtUploadSpeed;
    private long rtDownloadSpeed;
    private String hash;
    private String id;
    private String name;
    private long size;
    private long completedSize;

    public TorrentImpl(String id, String name, String hash, long size, long completedSize, double progress, long rtUploadSpeed, long rtDownloadSpeed, boolean privateTorrent) {
        this.id = id;
        this.name = name;
        this.hash = hash;
        this.size = size;
        this.completedSize = completedSize;
        this.progress = progress;
        this.rtUploadSpeed = rtUploadSpeed;
        this.rtDownloadSpeed = rtDownloadSpeed;
        this.privateTorrent = privateTorrent;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getCompletedSize() {
        return completedSize;
    }

    @Override
    public long getRtUploadSpeed() {
        return rtUploadSpeed;
    }

    @Override
    public long getRtDownloadSpeed() {
        return rtDownloadSpeed;
    }

    @Override
    public boolean isPrivate() {
        return privateTorrent;
    }
}
