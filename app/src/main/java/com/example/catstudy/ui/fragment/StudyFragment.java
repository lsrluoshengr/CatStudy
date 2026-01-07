package com.example.catstudy.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.ui.adapter.StudyCourseAdapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.catstudy.db.CheckInDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.AlertDialog;

public class StudyFragment extends Fragment {

    private RecyclerView recyclerView;
    private CourseDao courseDao;
    private CheckInDao checkInDao;
    private StudyCourseAdapter adapter;
    
    private View tabSelf;
    private TextView tvTabSelf;
    private View indicatorSelf;
    private TextView btnSignin;
    
    private List<Course> allCourses;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        
        // Init DAOs first
        courseDao = new CourseDao(getContext());
        checkInDao = new CheckInDao(getContext());
        SharedPreferences sp = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        currentUserId = sp.getInt("user_id", -1);
        
        initViews(view);
        initData();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        tabSelf = view.findViewById(R.id.tab_self_selected);
        tvTabSelf = view.findViewById(R.id.tv_tab_self);
        indicatorSelf = view.findViewById(R.id.indicator_tab_self);
        
        btnSignin = view.findViewById(R.id.btn_signin);
        if (btnSignin != null) {
            btnSignin.setOnClickListener(v -> handleCheckIn());
        }
            
        tabSelf.setOnClickListener(v -> switchTab(0));
    }
    
    private void handleCheckIn() {
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (checkInDao.isCheckedIn(currentUserId, today)) {
            showCheckInStatusDialog(); // Show status if already checked in
            return;
        }

        // Preview Reward (Optional, but user asked for "Reward Preview")
        // Since it's a click action, maybe just check in immediately or show confirmation?
        // Requirement: "Add Check-in Reward Preview function".
        // I'll check in immediately for better UX, but show reward in the success dialog.
        
        performCheckIn(today);
    }

    private void performCheckIn(String date) {
        // Simulate Network Sync
        // In real app, call API here. If success, save local.
        // Here we just save local.
        
        boolean success = checkInDao.checkIn(currentUserId, date, 10);
        if (success) {
            int consecutive = checkInDao.getConsecutiveDays(currentUserId);
            showSuccessDialog(10, consecutive);
            updateCheckInUI();
        } else {
            // Check if it failed because already checked in
            if (checkInDao.isCheckedIn(currentUserId, date)) {
                 showCheckInStatusDialog();
                 updateCheckInUI();
            } else {
                Toast.makeText(getContext(), "签到失败，请稍后重试", Toast.LENGTH_SHORT).show();
                android.util.Log.e("StudyFragment", "CheckIn failed for user " + currentUserId + " on " + date);
            }
        }
    }

    private void showSuccessDialog(int points, int days) {
        new AlertDialog.Builder(getContext())
            .setTitle("签到成功")
            .setMessage("获得积分：+" + points + "\n已连续签到：" + days + "天")
            .setPositiveButton("开心收下", null)
            .show();
    }
    
    private void showCheckInStatusDialog() {
        int consecutive = checkInDao.getConsecutiveDays(currentUserId);
         new AlertDialog.Builder(getContext())
            .setTitle("今日已签到")
            .setMessage("已连续签到：" + consecutive + "天\n明日签到可得 10 积分")
            .setPositiveButton("知道了", null)
            .show();
    }

    private void updateCheckInUI() {
        if (btnSignin == null) return;
        
        if (currentUserId == -1) {
            btnSignin.setText("签到 +10");
            btnSignin.setAlpha(1.0f);
            return;
        }
        
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean isCheckedIn = checkInDao.isCheckedIn(currentUserId, today);
        
        if (isCheckedIn) {
            btnSignin.setText("已签到");
            btnSignin.setAlpha(0.6f);
            // We keep it clickable to show status dialog
        } else {
            btnSignin.setText("签到 +10");
            btnSignin.setAlpha(1.0f);
        }
    }

    private void initData() {
        allCourses = courseDao.getAllCourses();
        switchTab(0);
        updateCheckInUI();
    }

    private List<Course> generateMockCourses() {
        return com.example.catstudy.data.MockCourseData.getMockCourses();
    }
    
    private void switchTab(int index) {
        tvTabSelf.setTextColor(Color.parseColor("#4CAF50")); // Green
        indicatorSelf.setVisibility(View.VISIBLE);
        updateList(allCourses);
    }
    
    private void updateList(List<Course> list) {
        adapter = new StudyCourseAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            SharedPreferences sp = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            currentUserId = sp.getInt("user_id", -1);
            updateCheckInUI();
        }
    }
}
