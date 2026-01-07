package com.example.catstudy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import com.example.catstudy.R;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.ui.adapter.CourseAdapter;
import com.example.catstudy.ui.adapter.ImageAdapter;
import com.example.catstudy.ui.adapter.RemoteImageAdapter;
import com.example.catstudy.ui.viewmodel.HomeViewModel;
import com.example.catstudy.data.MockCourseData;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.catstudy.util.CourseCoverUtils;

import com.example.catstudy.ui.activity.SearchActivity;
import com.example.catstudy.ui.activity.CourseDetailActivity;
import com.example.catstudy.network.ApiClient;
import com.example.catstudy.network.ApisApi;
import com.example.catstudy.db.CheckInDao;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.content.SharedPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Banner banner;
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private CourseDao courseDao;
    private HomeViewModel viewModel;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        courseDao = new CourseDao(getContext());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Setup Refresh Button
        View btnRefresh = view.findViewById(R.id.btn_refresh_covers);
        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(v -> refreshCourseCovers(true));
        }

        // Setup Banner
        banner = view.findViewById(R.id.banner);
        setupBanner();
        fetchRemoteBannerImages();

        // Setup RecyclerView with Mock Data
        recyclerView = view.findViewById(R.id.recycler_view_home);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        courseAdapter = new CourseAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        
        viewModel.getRecommended().observe(getViewLifecycleOwner(), list -> {
            courseAdapter.setNewData(list);
            updateBannerData(list);
        });
        
        viewModel.load();

        View searchEntry = view.findViewById(R.id.et_search);
        searchEntry.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        View catFood = view.findViewById(R.id.cat_food);
        View catMedical = view.findViewById(R.id.cat_medical);
        View catBehavior = view.findViewById(R.id.cat_behavior);
        View catDaily = view.findViewById(R.id.cat_daily);

        catFood.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("prefill_keyword", "排毒美食");
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        catMedical.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("prefill_keyword", "医疗百科");
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        catBehavior.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("prefill_keyword", "行为心理");
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        catDaily.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("prefill_keyword", "日常护理");
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (banner != null) banner.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (banner != null) banner.stop();
    }

    private void setupBanner() {
        // Initial setup with placeholder or empty
        banner.setIndicator(new CircleIndicator(getContext()));
    }

    private void fetchRemoteBannerImages() {
        ApisApi api = ApiClient.createService(ApisApi.class);
        api.getImages().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> images = response.body();
                if (images == null || images.isEmpty()) return;
                banner.setAdapter(new RemoteImageAdapter(images))
                      .setIndicator(new CircleIndicator(getContext()))
                      .start();
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) { }
        });
    }



    private void updateBannerData(List<Course> courses) {
        if (courses == null || courses.isEmpty()) return;

        // Pick up to 3 courses
        List<Course> bannerCourses = new ArrayList<>();

        for (Course c : courses) {
            if (bannerCourses.size() >= 3) break;
            bannerCourses.add(c);
        }

        if (bannerCourses.isEmpty()) return;

        banner.setAdapter(new ImageAdapter(bannerCourses))
              .setIndicator(new CircleIndicator(getContext()))
              .setOnBannerListener((data, position) -> {
                  if (position >= 0 && position < bannerCourses.size()) {
                      Course c = bannerCourses.get(position);
                      Intent intent = new Intent(getContext(), CourseDetailActivity.class);
                      intent.putExtra("course_id", c.getCourseId());
                      intent.putExtra("course_title", c.getTitle());
                      startActivity(intent);
                  }
              })
              .start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshCourseCovers(boolean force) {
        // Local resources are static, no refresh needed.
    }
}
