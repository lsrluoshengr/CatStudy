package com.example.catstudy.model;

public class News {
    private int newsId;
    private String authorName;
    private String content;
    private String imageUrl;
    private long publishTime;

    public News() {}

    public News(int newsId, String authorName, String content, String imageUrl, long publishTime) {
        this.newsId = newsId;
        this.authorName = authorName;
        this.content = content;
        this.imageUrl = imageUrl;
        this.publishTime = publishTime;
    }

    // Getters and Setters
    public int getNewsId() { return newsId; }
    public void setNewsId(int newsId) { this.newsId = newsId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public long getPublishTime() { return publishTime; }
    public void setPublishTime(long publishTime) { this.publishTime = publishTime; }
}
