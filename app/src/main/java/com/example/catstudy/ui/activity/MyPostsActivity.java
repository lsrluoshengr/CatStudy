package com.example.catstudy.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.db.SocialDao;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.Post;
import com.example.catstudy.model.User;
import com.example.catstudy.model.DiscoverPost;
import com.example.catstudy.ui.adapter.DiscoverAdapter;
import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        initToolbar("我的帖子");

        RecyclerView rv = findViewById(R.id.rv_my_posts);
        rv.setLayoutManager(new LinearLayoutManager(this));
        int uid = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        SocialDao socialDao = new SocialDao(this);
        UserDao userDao = new UserDao(this);
        List<Post> list = uid == -1 ? new ArrayList<>() : socialDao.getUserPosts(uid);
        User me = uid == -1 ? null : userDao.getUser(uid);
        String avatar = me != null ? me.getAvatarUrl() : "https://api.dicebear.com/7.x/avataaars/png?seed=Me";
        String name = me != null ? me.getNickname() : "我";
        List<DiscoverPost> dp = new ArrayList<>();
        for (Post p : list) {
            dp.add(new DiscoverPost(
                    p.getId(),
                    name,
                    avatar,
                    p.getContent(),
                    p.getImageUrl() == null ? "" : p.getImageUrl(),
                    ""
            ));
        }
        rv.setAdapter(new DiscoverAdapter(this, dp));
    }
}
