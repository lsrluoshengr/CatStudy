package com.example.catstudy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.model.DiscoverPost;
import com.example.catstudy.ui.adapter.DiscoverAdapter;
import com.example.catstudy.db.SocialDao;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.Post;
import com.example.catstudy.model.User;
import android.content.Intent;
import com.example.catstudy.ui.activity.CreatePostActivity;
import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiscoverAdapter adapter;
    private SocialDao socialDao;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_discover);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        socialDao = new SocialDao(getContext());
        userDao = new UserDao(getContext());

        adapter = new DiscoverAdapter(getContext(), generateListData());
        recyclerView.setAdapter(adapter);

        View fab = view.findViewById(R.id.fab_create_post);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new DiscoverAdapter(getContext(), generateListData());
        recyclerView.setAdapter(adapter);
    }

    private List<DiscoverPost> generateListData() {
        List<DiscoverPost> all = new ArrayList<>();
        List<Post> dbPosts = socialDao.getAllPosts();

        for (Post p : dbPosts) {
            User u = userDao.getUser(p.getUserId());
            String name = (u != null) ? u.getNickname() : "Unknown";
            String avatar = (u != null) ? u.getAvatarUrl() : "";
            if (avatar == null || avatar.isEmpty()) {
                avatar = "https://api.dicebear.com/7.x/avataaars/png?seed=" + name;
            }

            all.add(new DiscoverPost(
                    p.getId(),
                    name,
                    avatar,
                    p.getContent(),
                    p.getImageUrl(),
                    formatTime(p.getCreateTime())
            ));
        }
        return all;
    }

    private String formatTime(long time) {
        long diff = System.currentTimeMillis() - time;
        if (diff < 60000) return "刚刚";
        if (diff < 3600000) return (diff / 60000) + "分钟前";
        if (diff < 86400000) return (diff / 3600000) + "小时前";
        return (diff / 86400000) + "天前";
    }
}
