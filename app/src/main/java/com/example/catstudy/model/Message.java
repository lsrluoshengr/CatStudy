package com.example.catstudy.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {

    private String content;
    private final boolean isUser;      // true=用户发送, false=AI回复
    private final String timestamp;    // 消息时间
    private boolean isComplete;        // true=生成完毕, false=正在流式生成

    /**
     * 完整构造函数
     * @param content 消息内容
     * @param isUser 是否为用户
     * @param timestamp 时间字符串
     */
    public Message(String content, boolean isUser, String timestamp) {
        this.content = content;
        this.isUser = isUser;
        this.timestamp = timestamp;
        // 如果是用户发送的消息，默认直接标记为“完成”
        // 如果是AI发送的消息，初始化时默认为“未完成”（等待流式写入），除非传入的内容已经是完整的
        this.isComplete = isUser;
    }

    /**
     * [推荐] 便捷构造函数：自动生成当前时间
     * @param content 消息内容 (如果是AI开始回复，可以传空字符串 "")
     * @param isUser 是否为用户
     */
    public Message(String content, boolean isUser) {
        this(content, isUser, new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
    }

    // --- Getters ---

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isComplete() {
        return isComplete;
    }

    // --- Setters / 逻辑方法 ---

    /**
     * 设置完整内容 (用于一次性更新)
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 追加内容 (用于大模型流式输出，一个字一个字蹦出来)
     */
    public void appendContent(String newText) {
        if (newText != null) {
            this.content += newText;
        }
    }

    /**
     * 手动设置完成状态
     */
    public void setComplete(boolean complete) {
        this.isComplete = complete;
    }

    /**
     * 标记为已完成 (移除光标)
     */
    public void markComplete() {
        this.isComplete = true;
    }
}