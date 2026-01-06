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
import com.example.catstudy.ui.activity.CourseDetailActivity;
import java.util.List;

import com.example.catstudy.util.CourseCoverUtils;

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
        Glide.with(context)
             .load(coverResId)
             .placeholder(R.mipmap.ic_launcher)
             .error(R.mipmap.ic_launcher)
             .into(holder.ivCover);

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
