package com.example.catstudy.model;

public class Chapter {
    private int chapterId;
    private int courseId;
    private String title;
    private String videoUrl;
    private long duration;
    private int sortOrder;

    // Constructors
    public Chapter() {}

    public Chapter(int courseId, String title, String videoUrl, long duration, int sortOrder) {
        this.courseId = courseId;
        this.title = title;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters
    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
