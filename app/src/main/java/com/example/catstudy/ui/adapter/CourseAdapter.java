package com.example.catstudy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.model.Course;
import com.example.catstudy.network.ApiClient;
import com.example.catstudy.ui.activity.CourseDetailActivity;
import java.util.List;

import com.example.catstudy.util.CourseCoverUtils;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.annotation.Nullable;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    public void setNewData(List<Course> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvPoints.setText(course.getPrice() + "积分");
        holder.tvEnrollment.setText(course.getEnrollmentCount() + "人已学");
        
        int coverResId = CourseCoverUtils.getCoverResId(context, course.getCourseId());
        holder.ivCover.setContentDescription(course.getTitle()); // 添加 Alt 文本描述
        
        String coverUrl = course.getCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            String tempUrl = coverUrl;
            if (!coverUrl.startsWith("http") && !coverUrl.startsWith("content") && !coverUrl.startsWith("file")) {
                // Assume it's a relative path from API
                if (coverUrl.startsWith("/")) {
                     tempUrl = ApiClient.BASE_URL + coverUrl.substring(1);
                } else {
                     tempUrl = ApiClient.BASE_URL + coverUrl;
                }
            }
            final String fullUrl = tempUrl;
            
            Glide.with(context)
                 .load(fullUrl)
                 .placeholder(R.mipmap.ic_launcher)
                 .error(coverResId != 0 ? coverResId : R.mipmap.ic_launcher)
                 .listener(new RequestListener<Drawable>() { // 添加监听器
                      @Override
                      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                          // 这里会打印具体的错误原因到 Logcat
                          Log.e("GLIDE_ERROR", "加载失败: " + fullUrl, e);
                          if (e != null) {
                              e.logRootCauses("GLIDE_ERROR");
                          }
                          return false; // 返回 false 让 Glide 继续处理 error 占位图
                      }

                      @Override
                      public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                          return false;
                      }
                  })
                 .into(holder.ivCover);
        } else {
            Glide.with(context)
                 .load(coverResId)
                 .placeholder(R.mipmap.ic_launcher)
                 .error(R.mipmap.ic_launcher)
                 .into(holder.ivCover);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseDetailActivity.class);
            intent.putExtra("course_id", course.getCourseId());
            intent.putExtra("course_title", course.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvPoints;
        TextView tvEnrollment;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPoints = itemView.findViewById(R.id.tv_points);
            tvEnrollment = itemView.findViewById(R.id.tv_enrollment);
        }
    }
}
