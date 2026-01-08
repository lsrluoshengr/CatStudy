package com.example.catstudy.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.User;
import com.example.catstudy.ui.activity.LoginActivity;
import com.example.catstudy.ui.activity.MyPointsActivity;
import com.example.catstudy.ui.activity.OrderListActivity;
import com.example.catstudy.ui.activity.ShoppingCartActivity;
import com.example.catstudy.ui.activity.FavoritesActivity;
import com.example.catstudy.ui.activity.MyPostsActivity;
import com.example.catstudy.ui.activity.EditProfileActivity;
import com.example.catstudy.ui.activity.SettingsActivity;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvUserId;
    private TextView tvCoinsTotal;
    private TextView tvCoinsToday;
    private View headerProfile;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        headerProfile = view.findViewById(R.id.header_profile);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvUserId = view.findViewById(R.id.tv_user_id);
        tvCoinsTotal = view.findViewById(R.id.tv_coins_total);
        tvCoinsToday = view.findViewById(R.id.tv_coins_today);

        userDao = new UserDao(getContext());
        
        headerProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        View entryCart = view.findViewById(R.id.entry_cart);
        View entryOrders = view.findViewById(R.id.entry_orders);
        View entryFavorites = view.findViewById(R.id.entry_favorites);
        View entryFeedback = view.findViewById(R.id.entry_feedback);
        View entrySettings = view.findViewById(R.id.entry_settings);
        View btnLogout = view.findViewById(R.id.btn_logout);
        View cardPoints = view.findViewById(R.id.card_points);

        cardPoints.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyPointsActivity.class);
            startActivity(intent);
        });

        entryCart.setOnClickListener(v -> startActivity(new Intent(getContext(), ShoppingCartActivity.class)));
        entryOrders.setOnClickListener(v -> startActivity(new Intent(getContext(), OrderListActivity.class)));
        entryFavorites.setOnClickListener(v -> startActivity(new Intent(getContext(), FavoritesActivity.class)));
        entryFeedback.setOnClickListener(v -> Toast.makeText(getContext(), "正在联系客服...", Toast.LENGTH_SHORT).show());
        entrySettings.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadUserData();
        }
    }

    private void loadUserData() {
        SharedPreferences sp = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        
        if (userId != -1) {
            User user = userDao.getUser(userId);
            if (user != null) {
                tvNickname.setText(user.getNickname());
                tvUserId.setText("ID: " + user.getUserId());
                tvCoinsTotal.setText(String.valueOf(user.getCoins()));
                tvCoinsToday.setText("0");
                
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Glide.with(this)
                         .load(user.getAvatarUrl())
                         .placeholder(R.mipmap.ic_launcher)
                         .into(ivAvatar);
                }
            }
        }
    }

    private void logout() {
        SharedPreferences sp = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
