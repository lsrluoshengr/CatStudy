package com.example.catstudy.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.ui.adapter.StudyCourseAdapter;
import java.util.ArrayList;
import java.util.List;

public class StudyFragment extends Fragment {

    private RecyclerView recyclerView;
    private CourseDao courseDao;
    private StudyCourseAdapter adapter;
    
    private View tabSelf;
    private TextView tvTabSelf;
    private View indicatorSelf;
    
    private List<Course> allCourses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        
        initViews(view);
        initData();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        tabSelf = view.findViewById(R.id.tab_self_selected);
        tvTabSelf = view.findViewById(R.id.tv_tab_self);
        indicatorSelf = view.findViewById(R.id.indicator_tab_self);
        
        View btnSignin = view.findViewById(R.id.btn_signin);
        if (btnSignin != null) {
            btnSignin.setOnClickListener(v -> 
                Toast.makeText(getContext(), "签到成功！", Toast.LENGTH_SHORT).show());
        }
            
        tabSelf.setOnClickListener(v -> switchTab(0));
    }

    private void initData() {
        courseDao = new CourseDao(getContext());
        allCourses = courseDao.getAllCourses();
        switchTab(0);
    }

    private List<Course> generateMockCourses() {
        return com.example.catstudy.data.MockCourseData.getMockCourses();
    }
    
    private void switchTab(int index) {
        tvTabSelf.setTextColor(Color.parseColor("#4CAF50")); // Green
        indicatorSelf.setVisibility(View.VISIBLE);
        updateList(allCourses);
    }
    
    private void updateList(List<Course> list) {
        adapter = new StudyCourseAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
