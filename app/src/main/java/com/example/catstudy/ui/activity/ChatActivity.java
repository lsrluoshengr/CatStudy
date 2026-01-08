package com.example.catstudy.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catstudy.R;
import com.example.catstudy.model.Message;
import com.example.catstudy.network.SparkApiHelper;
import com.example.catstudy.ui.adapter.MessageAdapter;

import java.util.ArrayList;

public class ChatActivity extends BaseActivity { // 继承 BaseActivity 以保持统一风格

    private RecyclerView recyclerView;
    private EditText etInput;
    private Button btnSend;
    private MessageAdapter adapter;
    private boolean isGenerating = false; // 防止重复点击

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 假设 BaseActivity 有这个方法，如果没有可以删掉或自己实现 Toolbar
        initToolbar("AI 学习助手");

        recyclerView = findViewById(R.id.recycler_view_chat);
        etInput = findViewById(R.id.et_chat_input);
        btnSend = findViewById(R.id.btn_send);

        // 初始化 Adapter
        adapter = new MessageAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 发送按钮点击事件
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        if (isGenerating) {
            Toast.makeText(this, "AI 正在思考中，请稍候...", Toast.LENGTH_SHORT).show();
            return;
        }

        String question = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(question)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. 添加用户的消息到界面
        adapter.addMessage(new Message(question, true));
        etInput.setText("");
        scrollToBottom();

        // 2. 添加一个空的 AI 消息占位符 (用于流式显示)
        // 此时 isComplete = false，Adapter 会显示光标
        adapter.addMessage(new Message("", false));
        scrollToBottom();

        isGenerating = true;

        // 3. 调用 SparkApiHelper 发送请求
        SparkApiHelper.sendRequest(question, new SparkApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // 生成结束 (WebSocket 关闭)
                runOnUiThread(() -> {
                    // 标记最后一条消息为完成状态 (移除光标)
                    adapter.updateLastMessage(null, true);
                    isGenerating = false;
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "请求失败: " + error, Toast.LENGTH_SHORT).show();
                    // 移除那个空的占位消息，或者显示错误提示
                    isGenerating = false;
                    adapter.updateLastMessage("[网络错误，请重试]", true);
                });
            }

            @Override
            public void onPartialResult(String partialText) {
                // 收到流式数据片段
                runOnUiThread(() -> {
                    // 追加内容，保持 isComplete = false
                    adapter.updateLastMessage(partialText, false);
                    scrollToBottom();
                });
            }
        });
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }
}