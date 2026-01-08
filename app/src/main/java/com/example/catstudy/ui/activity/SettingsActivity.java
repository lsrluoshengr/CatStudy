package com.example.catstudy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.catstudy.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Hide ActionBar if exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Back
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // List Items
        findViewById(R.id.item_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        findViewById(R.id.item_general).setOnClickListener(v -> {
            Toast.makeText(this, "通用设置功能开发中", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.item_about).setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });

        // Buttons
        findViewById(R.id.btn_switch_account).setOnClickListener(v -> logout());
        findViewById(R.id.btn_logout).setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}