package com.example.catstudy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.model.Course;
import com.example.catstudy.db.CourseDao;
import java.util.List;

import com.example.catstudy.ui.activity.CourseDetailActivity;
import android.content.Intent;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import com.example.catstudy.db.ChapterDao;
import com.example.catstudy.model.Chapter;
import java.util.ArrayList;

import com.example.catstudy.util.CourseCoverUtils;

@OptIn(markerClass = UnstableApi.class)
public class StudyCourseAdapter extends RecyclerView.Adapter<StudyCourseAdapter.ViewHolder> {

    private Context context;
    private List<Course> courseList;

    public StudyCourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_study_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.tvTitle.setText(course.getTitle());
        
        // Use category as subtitle (e.g., "0人购买")
        String subtitle = course.getCategory();
        if (subtitle == null || subtitle.isEmpty()) {
            subtitle = "暂无介绍";
        }
        holder.tvSubtitle.setText(subtitle);
        
        int progress = course.getProgress();
        if (progress == 0) progress = 0;
        
        holder.progressBar.setProgress(progress);
        holder.tvProgressText.setText(progress + "%");
        
        int coverResId = CourseCoverUtils.getCoverResId(context, course.getCourseId());
        holder.ivCover.setContentDescription(course.getTitle()); // 添加 Alt 文本描述
        Glide.with(context)
             .load(coverResId)
             .placeholder(R.mipmap.ic_launcher)
             .centerCrop()
             .into(holder.ivCover);
        
        SharedPreferences sp = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String lastTitle = sp.getString("last_chapter_title_" + course.getCourseId(), "");
        holder.tvLastChapter.setText(lastTitle.isEmpty() ? "最近学习：暂无" : "最近学习：" + lastTitle);

        holder.itemView.setOnClickListener(v -> {
            CourseDao dao = new CourseDao(context);
            List<Course> matched = dao.searchCourses(course.getTitle());
            Course open = matched.isEmpty() ? null : matched.get(0);
            if (open == null) {
                List<Course> all = dao.getAllCourses();
                if (!all.isEmpty()) open = all.get(0);
            }
            if (open != null) {
                Intent intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtra("course_id", open.getCourseId());
                intent.putExtra("course_title", open.getTitle());
                context.startActivity(intent);
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            } else {
                Toast.makeText(context, "暂无课程数据", Toast.LENGTH_SHORT).show();
            }
        });
        
        holder.btnContinue.setOnClickListener(v -> {
            CourseDao dao = new CourseDao(context);
            List<Course> matched = dao.searchCourses(course.getTitle());
            Course open = matched.isEmpty() ? null : matched.get(0);
            int openId = open != null ? open.getCourseId() : -1;
            ChapterDao chapterDao = new ChapterDao(context);
            List<Chapter> chapters = openId != -1 ? chapterDao.getChaptersByCourseId(openId) : new java.util.ArrayList<>();
            Chapter target = null;
            String last = sp.getString("last_chapter_title_" + course.getCourseId(), "");
            if (!last.isEmpty()) {
                for (Chapter c : chapters) {
                    if (last.equals(c.getTitle())) { target = c; break; }
                }
            }
            if (target == null && !chapters.isEmpty()) target = chapters.get(0);
            if (target != null) {
                Intent intent = new Intent(context, com.example.catstudy.ui.activity.VideoPlayerActivity.class);
                intent.putExtra("extra_video_url", target.getVideoUrl());
                intent.putExtra("extra_chapter_title", target.getTitle());
                intent.putExtra("extra_course_id", openId);
                intent.putExtra("extra_chapter_id", target.getChapterId());
                context.startActivity(intent);
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            } else {
                Toast.makeText(context, "暂无章节数据", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvSubtitle, tvProgressText;
        ProgressBar progressBar;
        TextView tvLastChapter;
        Button btnContinue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_course_cover);
            tvTitle = itemView.findViewById(R.id.tv_course_title);
            tvSubtitle = itemView.findViewById(R.id.tv_course_subtitle);
            tvProgressText = itemView.findViewById(R.id.tv_progress_text);
            progressBar = itemView.findViewById(R.id.pb_progress);
            tvLastChapter = itemView.findViewById(R.id.tv_last_chapter);
            btnContinue = itemView.findViewById(R.id.btn_continue);
        }
    }
}
