package com.example.qrcode.model;

public class QRCodeModel {
    private int id;
    private String content;
    private String type;
    private long timestamp;
    private boolean isFavorite;
    private boolean isScanned; // true if scanned, false if generated

    public QRCodeModel() {}

    public QRCodeModel(String content, String type, long timestamp, boolean isFavorite, boolean isScanned) {
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.isFavorite = isFavorite;
        this.isScanned = isScanned;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public boolean isScanned() { return isScanned; }
    public void setScanned(boolean scanned) { isScanned = scanned; }
}
