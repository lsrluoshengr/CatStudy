package com.example.catstudy.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private int coins;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private String signature;

    public User() {}

    public User(int userId, String username, String password, String nickname, String avatarUrl, int coins) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.coins = coins;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
