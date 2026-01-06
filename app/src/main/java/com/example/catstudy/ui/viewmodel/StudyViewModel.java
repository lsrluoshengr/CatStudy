package com.example.catstudy.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.model.Course;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Course>> learningCourses = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, String>> lastChapterTitleMap = new MutableLiveData<>();

    public StudyViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Course>> getLearningCourses() { return learningCourses; }
    public LiveData<Map<Integer, String>> getLastChapterTitleMap() { return lastChapterTitleMap; }

    public void load() {
        Context context = getApplication();
        CourseDao courseDao = new CourseDao(context);
        List<Course> all = courseDao.getAllCourses();
        List<Course> learning = new ArrayList<>();
        for (Course c : all) {
            if (c.getProgress() > 0) learning.add(c);
        }
        SharedPreferences sp = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        Map<Integer, String> lastMap = new HashMap<>();
        for (Course c : learning) {
            String t = sp.getString("last_chapter_title_" + c.getCourseId(), "");
            lastMap.put(c.getCourseId(), t);
        }
        learningCourses.postValue(learning);
        lastChapterTitleMap.postValue(lastMap);
    }
}
