package com.example.catstudy;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.catstudy.ui.fragment.HomeFragment;
import com.example.catstudy.ui.fragment.StudyFragment;
import com.example.catstudy.ui.fragment.DiscoverFragment;
import com.example.catstudy.ui.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.catstudy.network.ApiClient;
import com.example.catstudy.network.ApisApi;
import com.example.catstudy.network.ApiResponse;
import com.example.catstudy.db.ChapterDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Sync chapter videos from API
        syncChapterVideos();
        // Sync course covers from API
        syncImages();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_study) {
                selectedFragment = new StudyFragment();
            } else if (itemId == R.id.nav_discover) {
                selectedFragment = new DiscoverFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            // For now, use HomeFragment as placeholder for others
            else {
                selectedFragment = new HomeFragment(); 
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default selection
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void syncChapterVideos() {
        ApisApi api = ApiClient.createService(ApisApi.class);
        api.getVideos().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> videos = response.body().getData();
                    if (videos != null && !videos.isEmpty()) {
                        // Filter out nulls
                        List<String> validVideos = new java.util.ArrayList<>();
                        for (String v : videos) {
                            if (v != null && !v.isEmpty()) {
                                validVideos.add(v);
                            }
                        }
                        
                        if (!validVideos.isEmpty()) {
                            ChapterDao chapterDao = new ChapterDao(MainActivity.this);
                            chapterDao.updateAllChapterVideos(validVideos);
                        } else {
                             android.util.Log.w("MainActivity", "No valid videos received from API");
                        }
                    } else {
                        android.util.Log.w("MainActivity", "Empty video list received from API");
                    }
                } else {
                    android.util.Log.e("MainActivity", "API response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                // Log error if needed
                android.util.Log.e("MainActivity", "Failed to sync videos: " + t.getMessage());
                // Optional: Toast for debugging
                // android.widget.Toast.makeText(MainActivity.this, "Sync videos failed: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncImages() {
        ApisApi api = ApiClient.createService(ApisApi.class);
        api.getImages().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> images = response.body().getData();
                    if (images != null && !images.isEmpty()) {
                        List<String> validImages = new java.util.ArrayList<>();
                        for (String img : images) {
                            if (img != null && !img.isEmpty()) {
                                validImages.add(img);
                            }
                        }
                        
                        if (!validImages.isEmpty()) {
                            com.example.catstudy.db.CourseDao courseDao = new com.example.catstudy.db.CourseDao(MainActivity.this);
                            courseDao.updateAllCourseCovers(validImages);
                            android.util.Log.d("MainActivity", "Synced " + validImages.size() + " images to courses");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                android.util.Log.e("MainActivity", "Failed to sync images: " + t.getMessage());
            }
        });
    }
}
