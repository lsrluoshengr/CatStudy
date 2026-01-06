package com.example.catstudy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.catstudy.R;
import com.example.catstudy.model.DiscoverPost;
import com.example.catstudy.db.SocialDao;
import com.example.catstudy.ui.activity.PostDetailActivity;
import java.util.List;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {

    private Context context;
    private List<DiscoverPost> postList;
    private SocialDao socialDao;
    private int currentUserId;

    public DiscoverAdapter(Context context, List<DiscoverPost> postList) {
        this.context = context;
        this.postList = postList;
        this.socialDao = new SocialDao(context);
        SharedPreferences sp = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        this.currentUserId = sp.getInt("user_id", -1);
    }

    @NonNull
    @Override
    public DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover_feed, parent, false);
        return new DiscoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverViewHolder holder, int position) {
        DiscoverPost post = postList.get(position);
        
        holder.tvUsername.setText(post.getUsername());
        holder.tvContent.setText(post.getContent());
        holder.tvTime.setText(post.getPublishTime());

        // Avatar with CircleCrop
        Glide.with(context)
             .load(post.getAvatarUrl())
             .apply(RequestOptions.bitmapTransform(new CircleCrop()))
             .placeholder(R.mipmap.ic_launcher)
             .into(holder.ivAvatar);

        // Post Image with RoundedCorners
        String rawImageUrl = post.getPostImageUrl();
        if (rawImageUrl != null && !rawImageUrl.isEmpty()) {
            String displayUrl = rawImageUrl.split(",")[0];
            holder.ivPostImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                 .load(displayUrl)
                 .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                 .placeholder(R.mipmap.ic_launcher)
                 .into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        int likeCount = socialDao.getLikeCount(post.getId());
        boolean liked = currentUserId != -1 && socialDao.hasLiked(currentUserId, post.getId());
        holder.tvLike.setText(String.valueOf(likeCount));
        holder.tvLike.setCompoundDrawablesWithIntrinsicBounds(
                liked ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off,
                0, 0, 0
        );

        int commentCount = socialDao.getCommentCount(post.getId());
        holder.tvComment.setText("评论 " + commentCount);

        holder.tvLike.setOnClickListener(v -> {
            if (currentUserId == -1) return;
            socialDao.toggleLike(currentUserId, post.getId());
            int lc = socialDao.getLikeCount(post.getId());
            boolean lk = socialDao.hasLiked(currentUserId, post.getId());
            holder.tvLike.setText(String.valueOf(lc));
            holder.tvLike.setCompoundDrawablesWithIntrinsicBounds(
                    lk ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off,
                    0, 0, 0
            );
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("username", post.getUsername());
            intent.putExtra("avatar_url", post.getAvatarUrl());
            intent.putExtra("content", post.getContent());
            intent.putExtra("image_url", post.getPostImageUrl());
            intent.putExtra("publish_time", post.getPublishTime());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class DiscoverViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivPostImage;
        TextView tvUsername, tvTime, tvContent, tvLike, tvComment, tvShare;

        public DiscoverViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvLike = itemView.findViewById(R.id.tv_like);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvShare = itemView.findViewById(R.id.tv_share);
        }
    }
}
