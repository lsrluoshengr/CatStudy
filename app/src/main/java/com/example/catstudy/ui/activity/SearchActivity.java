package com.example.catstudy.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.ui.adapter.CourseAdapter;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private EditText etInput;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private CourseAdapter courseAdapter;
    private CourseDao courseDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        courseDao = new CourseDao(this);

        ImageView ivBack = findViewById(R.id.iv_back);
        etInput = findViewById(R.id.et_input);
        recyclerView = findViewById(R.id.recycler_view_search);
        tvEmpty = findViewById(R.id.tv_empty);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        courseAdapter = new CourseAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(courseAdapter);

        ivBack.setOnClickListener(v -> finish());

        String prefill = getIntent().getStringExtra("prefill_keyword");
        if (prefill != null && !prefill.isEmpty()) {
            etInput.setText(prefill);
        } else {
            etInput.requestFocus();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && (prefill == null || prefill.isEmpty())) {
            imm.showSoftInput(etInput, InputMethodManager.SHOW_IMPLICIT);
        }

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                List<Course> results;
                if (keyword.isEmpty()) {
                    results = new ArrayList<>();
                } else {
                    results = courseDao.searchCourses(keyword);
                }
                courseAdapter.setNewData(results);
                if (results.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
