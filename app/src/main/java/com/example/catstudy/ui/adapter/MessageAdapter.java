package com.example.catstudy.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catstudy.R;
import com.example.catstudy.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_BOT = 1;

    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = (viewType == TYPE_USER) ? R.layout.item_message_user : R.layout.item_message_bot;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messages.get(position);

        // 设置时间 (如果不需要每条都显示时间，可以在这里加判断逻辑)
        if (holder.timestamp != null) {
            holder.timestamp.setText(msg.getTimestamp());
            holder.timestamp.setVisibility(View.GONE); // 暂时隐藏，看需求开启
        }

        // 处理大模型正在生成的“流式”效果
        if (!msg.isUser() && !msg.isComplete()) {
            // 如果是 AI 且未完成，添加一个模拟光标 "|"
            String contentWithCursor = msg.getContent() + " ▋";
            SpannableString spannable = new SpannableString(contentWithCursor);

            // 设置光标颜色 (例如设置为深红色或主色调)
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#b05535")),
                    spannable.length() - 1, spannable.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.content.setText(spannable);
        } else {
            // 正常显示
            holder.content.setText(msg.getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? TYPE_USER : TYPE_BOT;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * 添加一条新消息
     */
    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    /**
     * 更新最后一条消息的内容 (用于大模型流式输出更新)
     * @param content 新的内容片段或完整内容
     * @param isComplete 是否生成结束
     */
    public void updateLastMessage(String content, boolean isComplete) {
        if (!messages.isEmpty()) {
            int lastIndex = messages.size() - 1;
            Message lastMsg = messages.get(lastIndex);

            // 只有当最后一条是 Bot 的消息时才更新
            if (!lastMsg.isUser()) {
                lastMsg.setContent(content);
                lastMsg.setComplete(isComplete);
                notifyItemChanged(lastIndex); // 局部刷新，防止闪烁
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public TextView timestamp;

        public ViewHolder(View v) {
            super(v);
            content = v.findViewById(R.id.tv_message_content);
            timestamp = v.findViewById(R.id.tv_timestamp);
        }
    }
}