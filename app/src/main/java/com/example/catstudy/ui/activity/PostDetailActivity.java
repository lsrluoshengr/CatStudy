package com.example.catstudy.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.catstudy.R;
import com.example.catstudy.db.SocialDao;
import com.example.catstudy.model.Comment;
import com.example.catstudy.ui.adapter.CommentAdapter;
import java.util.List;

public class PostDetailActivity extends BaseActivity {
    private int postId;
    private String username;
    private String avatarUrl;
    private String content;
    private String imageUrl;
    private String publishTime;
    private int currentUserId;
    private SocialDao socialDao;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initToolbar("问题详情");

        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        LinearLayout containerImages = findViewById(R.id.container_images);
        TextView tvUsername = findViewById(R.id.tv_username);
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvContent = findViewById(R.id.tv_content);
        RecyclerView rvComments = findViewById(R.id.rv_comments);
        TextView tvCommentPlaceholder = findViewById(R.id.tv_comment_placeholder);
        ImageView ivUserAvatarBottom = findViewById(R.id.iv_user_avatar_bottom);

        postId = getIntent().getIntExtra("post_id", -1);
        username = getIntent().getStringExtra("username");
        avatarUrl = getIntent().getStringExtra("avatar_url");
        content = getIntent().getStringExtra("content");
        imageUrl = getIntent().getStringExtra("image_url");
        publishTime = getIntent().getStringExtra("publish_time");

        currentUserId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        socialDao = new SocialDao(this);

        tvUsername.setText(username != null ? username : "");
        // Use random source for demo
        tvTime.setText((publishTime != null ? publishTime : "") + " 发布于护理学基础");
        tvContent.setText(content != null ? content : "");

        Glide.with(this)
                .load(avatarUrl)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.mipmap.ic_launcher)
                .into(ivAvatar);

        // Load current user avatar for bottom bar
        // Ideally fetch from DB, here just placeholder or same as user if self
        Glide.with(this)
                .load("https://api.dicebear.com/7.x/avataaars/png?seed=User" + currentUserId)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.mipmap.ic_launcher)
                .into(ivUserAvatarBottom);

        // Parse and display images
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String[] urls = imageUrl.split(",");
            for (String url : urls) {
                if (url.trim().isEmpty()) continue;
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 
                        600); 
                params.setMargins(0, 16, 0, 16);
                iv.setLayoutParams(params);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                
                Glide.with(this)
                        .load(url.trim())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                        .placeholder(R.mipmap.ic_launcher)
                        .into(iv);
                containerImages.addView(iv);
            }
        }

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setNestedScrollingEnabled(false);
        List<Comment> comments = socialDao.getComments(postId);
        commentAdapter = new CommentAdapter(comments);
        rvComments.setAdapter(commentAdapter);

        tvCommentPlaceholder.setOnClickListener(v -> showCommentDialog());
    }

    private void showCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发表评论");
        final EditText input = new EditText(this);
        input.setHint("写下你的评论...");
        // Add padding
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);
        builder.setView(input);

        builder.setPositiveButton("发送", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (currentUserId == -1) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (text.isEmpty()) return;
            
            socialDao.addComment(currentUserId, postId, text);
            // Refresh comments
            List<Comment> comments = socialDao.getComments(postId);
            commentAdapter.setData(comments);
            Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
