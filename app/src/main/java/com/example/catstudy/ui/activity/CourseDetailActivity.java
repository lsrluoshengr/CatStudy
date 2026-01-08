package com.example.catstudy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import androidx.fragment.app.FragmentTransaction;
import com.example.catstudy.R;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.db.CartDao;
import com.example.catstudy.db.ChapterDao;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import com.example.catstudy.model.Course;
import com.example.catstudy.model.Chapter;
import com.example.catstudy.model.Review;
import com.example.catstudy.ui.fragment.CourseCatalogFragment;
import com.example.catstudy.util.CourseCoverUtils;
import com.google.android.material.tabs.TabLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import com.example.catstudy.db.FavoriteDao;
import com.example.catstudy.db.ReviewDao;
import com.example.catstudy.ui.adapter.ReviewAdapter;
import com.example.catstudy.network.ApiClient;
import com.example.catstudy.network.ApisApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import com.example.catstudy.network.ApiResponse;

@OptIn(markerClass = UnstableApi.class)
public class CourseDetailActivity extends BaseActivity {

    private int courseId;
    private List<String> remoteVideoUrls;
    private CourseDao courseDao;
    private CartDao cartDao;
    private ChapterDao chapterDao;
    private Course course;
    private ImageView ivCover;


    private ImageView ivFavorite;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private FavoriteDao favoriteDao;
    private ReviewDao reviewDao;
    private int currentUserId;
    private TextView tvCourseTitle;
    private TextView tvPoints;
    private TextView tvEnrollment;
    private Button btnTabIntro;
    private Button btnTabCatalog;
    private Button btnTabReviews;
    private View containerIntro;
    private View containerCatalog;
    private View containerReviews;
    private RatingBar rbInput;
    private EditText etInput;
    private Button btnSubmitReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        initToolbar("课程详情");
        
        ivCover = findViewById(R.id.iv_cover);
        ivFavorite = findViewById(R.id.iv_favorite);
        rvReviews = findViewById(R.id.rv_reviews);
        tvCourseTitle = findViewById(R.id.tv_course_title);
        tvPoints = findViewById(R.id.tv_points);
        tvEnrollment = findViewById(R.id.tv_enrollment);
        
        btnTabIntro = findViewById(R.id.btn_tab_intro);
        btnTabCatalog = findViewById(R.id.btn_tab_catalog);
        btnTabReviews = findViewById(R.id.btn_tab_reviews);
        
        containerIntro = findViewById(R.id.container_intro);
        containerCatalog = findViewById(R.id.container_catalog);
        containerReviews = findViewById(R.id.container_reviews);
        rbInput = findViewById(R.id.rb_input);
        etInput = findViewById(R.id.et_input);
        btnSubmitReview = findViewById(R.id.btn_submit_review);

        courseId = getIntent().getIntExtra("course_id", -1);
        if (courseId == -1) {
            Toast.makeText(this, "Invalid Course", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        courseDao = new CourseDao(this);
        favoriteDao = new FavoriteDao(this);
        reviewDao = new ReviewDao(this);
        cartDao = new CartDao(this);
        chapterDao = new ChapterDao(this);
        currentUserId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        course = courseDao.getCourse(courseId);
        
        if (course == null) {
            Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(course.getTitle());
        tvCourseTitle.setText(course.getTitle());
        tvPoints.setText(course.getPrice() + "积分");
        tvEnrollment.setText(course.getEnrollmentCount() + "人已学");

        boolean fav = currentUserId != -1 && favoriteDao.isFavorited(currentUserId, courseId);
        updateFavoriteUI(fav);
        ivFavorite.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            favoriteDao.toggleFavorite(currentUserId, courseId);
            boolean nowFav = favoriteDao.isFavorited(currentUserId, courseId);
            updateFavoriteUI(nowFav);
            Toast.makeText(this, nowFav ? "收藏成功" : "已取消收藏", Toast.LENGTH_SHORT).show();
        });

        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewDao.getReviews(courseId));
        rvReviews.setAdapter(reviewAdapter);

        btnSubmitReview.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            int rating = (int) rbInput.getRating();
            String content = etInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "请输入评价内容", Toast.LENGTH_SHORT).show();
                return;
            }
            Review r = new Review();
            r.setCourseId(courseId);
            r.setUserId(currentUserId);
            r.setRating(rating);
            r.setContent(content);
            r.setCreateTime(System.currentTimeMillis());
            reviewDao.addReview(r);
            etInput.setText("");
            reviewAdapter.setData(reviewDao.getReviews(courseId));
            Toast.makeText(this, "评价已提交", Toast.LENGTH_SHORT).show();
        });

        btnTabIntro.setOnClickListener(v -> showTab(0));
        btnTabCatalog.setOnClickListener(v -> showTab(1));
        btnTabReviews.setOnClickListener(v -> showTab(2));
        showTab(0);

        ((TextView) findViewById(R.id.tv_intro_text)).setText("课程简介：\n" + course.getTitle() + "\n分类：" + course.getCategory());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_catalog, new CourseCatalogFragment());
        ft.commit();

        // Set up cover image and play button
        setupCover();

        // Button Logic: Start Learning button now plays the first chapter
        Button btnStart = findViewById(R.id.btn_start_learning);
        btnStart.setOnClickListener(v -> {
            // Get the first chapter for this course and play it
            List<Chapter> chapters = chapterDao.getChaptersByCourseId(courseId);
            if (!chapters.isEmpty()) {
                Chapter firstChapter = chapters.get(0);
                Intent intent = new Intent(CourseDetailActivity.this, VideoPlayerActivity.class);
                intent.putExtra("extra_video_url", firstChapter.getVideoUrl());
                intent.putExtra("extra_chapter_title", firstChapter.getTitle());
                startActivity(intent);
            } else {
                Toast.makeText(this, "No chapters available", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            cartDao.addToCart(currentUserId, courseId);
            Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
        });
    }

    // Set up cover image
    private void setupCover() {
        // Load cover image using Glide
        int coverResId = CourseCoverUtils.getCoverResId(this, course.getCourseId());
        ivCover.setContentDescription(course.getTitle()); 
        
        String coverUrl = course.getCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
             String fullUrl = coverUrl;
             if (!coverUrl.startsWith("http") && !coverUrl.startsWith("content") && !coverUrl.startsWith("file")) {
                 if (coverUrl.startsWith("/")) {
                      fullUrl = ApiClient.BASE_URL + coverUrl.substring(1);
                 } else {
                      fullUrl = ApiClient.BASE_URL + coverUrl;
                 }
             }
             Glide.with(this)
                  .load(fullUrl)
                  .placeholder(R.mipmap.ic_launcher)
                  .error(coverResId != 0 ? coverResId : R.mipmap.ic_launcher)
                  .into(ivCover);
        } else {
            Glide.with(this)
                 .load(coverResId)
                 .placeholder(R.mipmap.ic_launcher)
                 .error(R.mipmap.ic_launcher)
                 .into(ivCover);
        }
    }

    // Add Glide import if needed

    private void showTab(int index) {
        containerIntro.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        containerCatalog.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        containerReviews.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
        
        updateTabButton(btnTabIntro, index == 0);
        updateTabButton(btnTabCatalog, index == 1);
        updateTabButton(btnTabReviews, index == 2);
    }

    private void updateTabButton(Button btn, boolean active) {
        if (active) {
            btn.setBackgroundResource(R.drawable.bg_classical_button);
            btn.setTextColor(getResources().getColor(R.color.classical_ivory));
        } else {
            btn.setBackgroundResource(R.drawable.bg_classical_inner_border);
            btn.setTextColor(getResources().getColor(R.color.classical_text_primary));
        }
    }

    private void updateFavoriteUI(boolean isFavorited) {
        ivFavorite.setImageResource(isFavorited ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        ivFavorite.setColorFilter(isFavorited ? 0xFFFFC107 : 0xFF9E9E9E);
    }

    private void fetchRemoteVideos() {
        ApisApi apisApi = ApiClient.createService(ApisApi.class);
        apisApi.getVideos().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> videos = response.body().getData();
                    if (videos != null && !videos.isEmpty()) {
                        List<String> validVideos = new java.util.ArrayList<>();
                        for (String v : videos) {
                            if (v != null && !v.isEmpty()) {
                                validVideos.add(v);
                            }
                        }
                        if (!validVideos.isEmpty()) {
                            remoteVideoUrls = validVideos;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                // Ignore failure
            }
        });
    }
}
