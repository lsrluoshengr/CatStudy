package com.example.catstudy.model;

public class DiscoverPost {
    private int id;
    private String username;
    private String avatarUrl;
    private String content;
    private String postImageUrl;
    private String publishTime;

    public DiscoverPost(int id, String username, String avatarUrl, String content, String postImageUrl, String publishTime) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.postImageUrl = postImageUrl;
        this.publishTime = publishTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPostImageUrl() { return postImageUrl; }
    public void setPostImageUrl(String postImageUrl) { this.postImageUrl = postImageUrl; }
    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }
}
