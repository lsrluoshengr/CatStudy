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
import com.example.catstudy.model.Course;
import com.example.catstudy.db.CourseDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Sync full course data from API
        syncCourses();

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

    private void syncCourses() {
        ApisApi api = ApiClient.createService(ApisApi.class);
        api.getCourses().enqueue(new Callback<ApiResponse<List<Course>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Course>>> call, Response<ApiResponse<List<Course>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Course> courses = response.body().getData();
                    if (courses != null && !courses.isEmpty()) {
                        CourseDao courseDao = new CourseDao(MainActivity.this);
                        courseDao.syncCourses(courses);
                        android.util.Log.d("MainActivity", "Synced " + courses.size() + " courses from API");
                    } else {
                        android.util.Log.w("MainActivity", "Empty course list received from API");
                    }
                } else {
                    android.util.Log.e("MainActivity", "API response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Course>>> call, Throwable t) {
                android.util.Log.e("MainActivity", "Failed to sync courses: " + t.getMessage());
            }
        });
    }
}
