package com.example.catstudy.ui.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catstudy.R;
import com.example.catstudy.db.SocialDao;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.DiscoverPost;
import com.example.catstudy.model.Message;
import com.example.catstudy.model.Post;
import com.example.catstudy.model.User;
import com.example.catstudy.network.SparkApiHelper;
import com.example.catstudy.ui.activity.CreatePostActivity;
import com.example.catstudy.ui.adapter.DiscoverAdapter;
import com.example.catstudy.ui.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscoverFragment extends Fragment {

    // --- 社区动态部分变量 ---
    private RecyclerView rvPosts;
    private DiscoverAdapter postAdapter;
    private SocialDao socialDao;
    private UserDao userDao;
    private View containerCommunity;
    private View fabCreatePost;

    // --- AI 聊天部分变量 ---
    private RecyclerView rvChat;
    private EditText etChatInput;
    private Button btnSendChat;
    private MessageAdapter chatAdapter;
    private List<Message> messages = new ArrayList<>();
    private int pendingMessagePosition = -1;
    private View containerChat;

    // --- Tab 切换变量 ---
    private TextView tvTabCommunity, tvTabAi;
    private int currentTab = 0; // 0=Community, 1=AI

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        // 1. 初始化视图控件
        initViews(view);

        // 2. 初始化社区数据
        initCommunityData();

        // 3. 初始化聊天功能
        initChatFeature();

        // 4. 设置 Tab 切换事件
        tvTabCommunity.setOnClickListener(v -> switchTab(0));
        tvTabAi.setOnClickListener(v -> switchTab(1));

        return view;
    }

    private void initViews(View view) {
        // Tab 按钮
        tvTabCommunity = view.findViewById(R.id.tv_tab_community);
        tvTabAi = view.findViewById(R.id.tv_tab_ai);

        // 容器
        containerCommunity = view.findViewById(R.id.container_community);
        containerChat = view.findViewById(R.id.container_chat);

        // 社区控件
        rvPosts = view.findViewById(R.id.recycler_view_discover);
        fabCreatePost = view.findViewById(R.id.fab_create_post);

        // 聊天控件
        rvChat = view.findViewById(R.id.recycler_view_chat);
        etChatInput = view.findViewById(R.id.et_chat_input);
        btnSendChat = view.findViewById(R.id.btn_send_chat);
    }

    // ================== 社区功能逻辑 ==================

    private void initCommunityData() {
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        socialDao = new SocialDao(getContext());
        userDao = new UserDao(getContext());

        postAdapter = new DiscoverAdapter(getContext(), generateListData());
        rvPosts.setAdapter(postAdapter);

        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 刷新帖子列表
        if (postAdapter != null) {
            postAdapter = new DiscoverAdapter(getContext(), generateListData());
            rvPosts.setAdapter(postAdapter);
        }
    }

    private List<DiscoverPost> generateListData() {
        List<DiscoverPost> all = new ArrayList<>();
        List<Post> dbPosts = socialDao.getAllPosts();

        for (Post p : dbPosts) {
            User u = userDao.getUser(p.getUserId());
            String name = (u != null) ? u.getNickname() : "Unknown";
            String avatar = (u != null) ? u.getAvatarUrl() : "";
            if (avatar == null || avatar.isEmpty()) {
                avatar = "https://api.dicebear.com/7.x/avataaars/png?seed=" + name;
            }

            all.add(new DiscoverPost(
                    p.getId(),
                    name,
                    avatar,
                    p.getContent(),
                    p.getImageUrl(),
                    formatTime(p.getCreateTime())
            ));
        }
        return all;
    }

    private String formatTime(long time) {
        long diff = System.currentTimeMillis() - time;
        if (diff < 60000) return "刚刚";
        if (diff < 3600000) return (diff / 60000) + "分钟前";
        if (diff < 86400000) return (diff / 3600000) + "小时前";
        return (diff / 86400000) + "天前";
    }

    // ================== AI 聊天逻辑 (集成自手册) ==================

    private void initChatFeature() {
        chatAdapter = new MessageAdapter(getContext(), messages); // 注意：这里适配你的构造函数
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(chatAdapter);

        btnSendChat.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String question = etChatInput.getText().toString().trim();
        if (question.isEmpty()) return;

        // 1. 添加用户消息
        addMessage(question, true);
        etChatInput.setText("");
        btnSendChat.setEnabled(false);

        // 2. 发起 API 请求
        SparkApiHelper.sendRequest(question, new SparkApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (pendingMessagePosition != -1 && pendingMessagePosition < messages.size()) {
                        messages.get(pendingMessagePosition).markComplete();
                        chatAdapter.notifyItemChanged(pendingMessagePosition);
                    }
                    btnSendChat.setEnabled(true);
                    pendingMessagePosition = -1;
                });
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (pendingMessagePosition != -1 && pendingMessagePosition < messages.size()) {
                        messages.get(pendingMessagePosition).markComplete();
                        messages.get(pendingMessagePosition).appendContent("\n\n[错误: " + error + "]");
                        chatAdapter.notifyItemChanged(pendingMessagePosition);
                    } else {
                        Toast.makeText(getContext(), "请求失败: " + error, Toast.LENGTH_SHORT).show();
                    }
                    btnSendChat.setEnabled(true);
                    pendingMessagePosition = -1;
                });
            }

            @Override
            public void onPartialResult(String partialText) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    // 如果还没有占位消息，先创建一条
                    if (pendingMessagePosition == -1) {
                        pendingMessagePosition = addPlaceholderMessage();
                    }
                    // 追加内容
                    Message currentMessage = messages.get(pendingMessagePosition);
                    currentMessage.appendContent(partialText);
                    chatAdapter.notifyItemChanged(pendingMessagePosition);
                    rvChat.smoothScrollToPosition(pendingMessagePosition);
                });
            }
        });
    }

    private void addMessage(String content, boolean isUser) {
        String timestamp = DateFormat.format("HH:mm", new Date()).toString();
        // 确保你的 Message 类构造函数匹配
        messages.add(new Message(content, isUser, timestamp));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        rvChat.smoothScrollToPosition(messages.size() - 1);
    }

    private int addPlaceholderMessage() {
        String timestamp = DateFormat.format("HH:mm", new Date()).toString();
        // 创建一个空的机器人消息作为占位
        Message placeholder = new Message("", false, timestamp);
        messages.add(placeholder);
        int position = messages.size() - 1;
        chatAdapter.notifyItemInserted(position);
        rvChat.smoothScrollToPosition(position);
        return position;
    }

    // ================== 界面切换逻辑 ==================

    private void switchTab(int index) {
        currentTab = index;
        if (index == 0) {
            // 选中社区
            tvTabCommunity.setTextColor(getResources().getColor(R.color.classical_gold));
            tvTabCommunity.setTypeface(null, Typeface.BOLD);
            tvTabAi.setTextColor(0xCCFFF8DC); // 半透明米色
            tvTabAi.setTypeface(null, Typeface.NORMAL);

            containerCommunity.setVisibility(View.VISIBLE);
            containerChat.setVisibility(View.GONE);
        } else {
            // 选中 AI
            tvTabAi.setTextColor(getResources().getColor(R.color.classical_gold));
            tvTabAi.setTypeface(null, Typeface.BOLD);
            tvTabCommunity.setTextColor(0xCCFFF8DC);
            tvTabCommunity.setTypeface(null, Typeface.NORMAL);

            containerCommunity.setVisibility(View.GONE);
            containerChat.setVisibility(View.VISIBLE);
        }
    }
}