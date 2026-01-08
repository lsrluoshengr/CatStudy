package com.example.catstudy.ui.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.catstudy.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        findViewById(R.id.item_intro).setOnClickListener(v -> {
            Toast.makeText(this, "功能介绍页面开发中", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.item_complaint).setOnClickListener(v -> {
            Toast.makeText(this, "投诉页面开发中", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.item_update).setOnClickListener(v -> {
            Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
        });
    }
}