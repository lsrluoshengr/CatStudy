package com.example.catstudy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.catstudy.R;

public class BaseActivity extends AppCompatActivity {

    protected void initToolbar(String title) {
        TextView tvTitle = findViewById(R.id.tv_title);
        ImageView ivBack = findViewById(R.id.iv_back);

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        if (title == null || title.isEmpty()) {
            if (tvTitle != null) {
                View toolbar = tvTitle.getParent() instanceof View ? (View) tvTitle.getParent() : null;
                if (toolbar != null) {
                    toolbar.setVisibility(View.GONE);
                }
            }
            return;
        }

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }
}
