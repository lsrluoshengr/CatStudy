package com.example.catstudy.model;

public class Course {
    private int courseId;
    private String title;
    private String coverUrl;
    private String videoUrl;
    private int progress;
    private String category;
    private int price;
    private int enrollmentCount;

    public Course() {}

    public Course(int courseId, String title, String coverUrl, String videoUrl, int progress, String category,int price,int enrollmentCount) {
        this.courseId = courseId;
        this.title = title;
        this.coverUrl = coverUrl;
        this.videoUrl = videoUrl;
        this.progress = progress;
        this.category = category;
        this.price=price;
        this.enrollmentCount=enrollmentCount;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(int enrollmentCount) { this.enrollmentCount = enrollmentCount; }
}
