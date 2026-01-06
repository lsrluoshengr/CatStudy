package com.example.catstudy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CourseIntroFragment extends Fragment {
    
    private String description;

    public static CourseIntroFragment newInstance(String desc) {
        CourseIntroFragment fragment = new CourseIntroFragment();
        Bundle args = new Bundle();
        args.putString("desc", desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        
        if (getArguments() != null) {
            description = getArguments().getString("desc", "No description available.");
        } else {
            description = "No description available.";
        }
        
        textView.setText(description);
        return textView;
    }
}
