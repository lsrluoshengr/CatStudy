package com.example.catstudy.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.model.Review;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH> {
    private List<Review> data;

    public ReviewAdapter(List<Review> data) {
        this.data = data;
    }

    public void setData(List<Review> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Review r = data.get(position);
        holder.tvContent.setText(r.getContent());
        holder.rb.setRating(r.getRating());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(r.getCreateTime()));
        holder.tvTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvTime;
        RatingBar rb;
        VH(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_review_content);
            tvTime = itemView.findViewById(R.id.tv_review_time);
            rb = itemView.findViewById(R.id.rb_review);
        }
    }
}
