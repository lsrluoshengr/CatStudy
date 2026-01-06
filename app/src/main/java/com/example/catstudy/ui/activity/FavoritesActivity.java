package com.example.catstudy.ui.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.db.FavoriteDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.ui.adapter.CourseAdapter;
import java.util.List;

public class FavoritesActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initToolbar("我的收藏");

        RecyclerView rv = findViewById(R.id.recycler_favorites);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        int userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        FavoriteDao favoriteDao = new FavoriteDao(this);
        List<Course> courses = favoriteDao.getMyFavorites(userId);
        CourseAdapter adapter = new CourseAdapter(this, courses);
        rv.setAdapter(adapter);
    }
}
