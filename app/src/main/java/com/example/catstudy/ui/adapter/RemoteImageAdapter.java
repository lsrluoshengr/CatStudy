package com.example.catstudy.ui.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.catstudy.network.ApiClient;
import com.youth.banner.adapter.BannerAdapter;
import java.util.List;

public class RemoteImageAdapter extends BannerAdapter<String, RemoteImageAdapter.BannerViewHolder> {

    public RemoteImageAdapter(List<String> datas) {
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
    public void onBindView(BannerViewHolder holder, String url, int position, int size) {
        String fullUrl = url;
        if (url != null && !url.isEmpty() && !url.startsWith("http") && !url.startsWith("content") && !url.startsWith("file")) {
             if (url.startsWith("/")) {
                  fullUrl = ApiClient.BASE_URL + url.substring(1);
             } else {
                  fullUrl = ApiClient.BASE_URL + url;
             }
        }
        Glide.with(holder.itemView)
                .load(fullUrl)
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
