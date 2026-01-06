package com.example.catstudy.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.db.SocialDao;
import com.example.catstudy.model.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreatePostActivity extends BaseActivity {
    private EditText etContent;
    private TextView tvImageCount;
    private RecyclerView rvImages;
    private ImageAdapter imageAdapter;
    private SocialDao socialDao;
    private int currentUserId;
    private List<String> selectedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initToolbar("发布动态");

        socialDao = new SocialDao(this);
        currentUserId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);

        etContent = findViewById(R.id.et_content);
        tvImageCount = findViewById(R.id.tv_image_count);
        rvImages = findViewById(R.id.rv_images);
        ImageView btnSend = findViewById(R.id.btn_send);

        // Setup RecyclerView
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter();
        rvImages.setAdapter(imageAdapter);

        btnSend.setOnClickListener(v -> publishPost());
    }

    private void publishPost() {
        String content = etContent.getText().toString().trim();
        if (currentUserId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        Post p = new Post();
        p.setUserId(currentUserId);
        p.setContent(content);
        // Use comma separated string for multiple images
        if (!selectedImages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String img : selectedImages) {
                if (sb.length() > 0) sb.append(",");
                sb.append(img);
            }
            p.setImageUrl(sb.toString());
        }
        p.setCreateTime(System.currentTimeMillis());
        socialDao.insertPost(p);
        Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    private class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ADD = 0;
        private static final int TYPE_IMAGE = 1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_ADD) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_add, parent, false);
                return new AddViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_selected, parent, false);
                return new ImageViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_ADD) {
                holder.itemView.setOnClickListener(v -> {
                    if (selectedImages.size() >= 6) {
                        Toast.makeText(CreatePostActivity.this, "最多选择6张图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Simulate adding an image
                    String[] pics = {
                            "https://picsum.photos/seed/" + new Random().nextInt(9999) + "/600/400",
                            "https://img.zcool.cn/community/01f09e577b85450000018c1b4f4c2e.jpg@1280w_1l_2o_100sh.jpg"
                    };
                    selectedImages.add(pics[new Random().nextInt(pics.length)]);
                    updateCount();
                    notifyDataSetChanged();
                });
            } else {
                ImageViewHolder ivh = (ImageViewHolder) holder;
                String url = selectedImages.get(position);
                Glide.with(ivh.itemView.getContext()).load(url).centerCrop().into(ivh.ivImage);
                ivh.ivDelete.setOnClickListener(v -> {
                    selectedImages.remove(position);
                    updateCount();
                    notifyDataSetChanged();
                });
            }
        }

        @Override
        public int getItemCount() {
            return selectedImages.size() < 6 ? selectedImages.size() + 1 : selectedImages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (selectedImages.size() < 6 && position == selectedImages.size()) {
                return TYPE_ADD;
            }
            return TYPE_IMAGE;
        }
    }

    private void updateCount() {
        tvImageCount.setText(selectedImages.size() + "/6");
    }

    class AddViewHolder extends RecyclerView.ViewHolder {
        AddViewHolder(View itemView) { super(itemView); }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageView ivDelete;
        ImageViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
