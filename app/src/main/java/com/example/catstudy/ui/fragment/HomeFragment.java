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
import com.example.catstudy.network.ApiResponse;
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
    private boolean hasRemoteBanner = false;
    
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
            intent.putExtra("prefill_keyword", "新手入门");
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        catMedical.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("prefill_keyword", "初级课程");
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        catBehavior.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("prefill_keyword", "中级课程");
            startActivity(intent);
            if (getActivity() != null) getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        catDaily.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("prefill_keyword", "高级课程");
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
        api.getImages().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (getContext() == null || !isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<String> images = response.body().getData();
                    if (images != null && !images.isEmpty()) {
                         List<String> validImages = new java.util.ArrayList<>();
                         for (String img : images) {
                             if (img != null && !img.isEmpty()) {
                                 validImages.add(img);
                                 // Test: Print image link to console
                                 System.out.println("Fetched Image URL: " + img);
                                 android.util.Log.d("HomeFragment", "Fetched Image URL: " + img);
                             }
                         }
                         if (!validImages.isEmpty()) {
                             banner.setAdapter(new RemoteImageAdapter(validImages))
                                   .setIndicator(new CircleIndicator(getContext()))
                                   .start();
                         }
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) { }
        });
    }



    private void updateBannerData(List<Course> courses) {
        if (hasRemoteBanner) return;
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
