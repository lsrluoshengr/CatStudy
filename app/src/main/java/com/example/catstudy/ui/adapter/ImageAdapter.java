package com.example.catstudy.ui.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.model.Course;
import com.example.catstudy.util.CourseCoverUtils;
import com.youth.banner.adapter.BannerAdapter;
import java.util.List;

public class ImageAdapter extends BannerAdapter<Course, ImageAdapter.BannerViewHolder> {

    public ImageAdapter(List<Course> datas) {
        super(datas);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, Course data, int position, int size) {
        int coverResId = CourseCoverUtils.getCoverResId(holder.itemView.getContext(), data.getCourseId());
        holder.imageView.setContentDescription(data.getTitle());
        Glide.with(holder.itemView)
                .load(coverResId)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imageView);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull ImageView view) {
            super(view);
            this.imageView = view;
        }
    }
}
