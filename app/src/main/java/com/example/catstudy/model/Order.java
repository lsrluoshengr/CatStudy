package com.example.catstudy.model;

import java.util.List;

public class Order {
    private int id;
    private int userId;
    private int totalCoins;
    private long createTime;
    private List<Course> courseList;

    public Order() {}

    public Order(int id, int userId, int totalCoins, long createTime) {
        this.id = id;
        this.userId = userId;
        this.totalCoins = totalCoins;
        this.createTime = createTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTotalCoins() { return totalCoins; }
    public void setTotalCoins(int totalCoins) { this.totalCoins = totalCoins; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public List<Course> getCourseList() { return courseList; }
    public void setCourseList(List<Course> courseList) { this.courseList = courseList; }
}
