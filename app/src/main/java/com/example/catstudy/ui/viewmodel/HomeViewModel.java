package com.example.catstudy.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.db.ReviewDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.model.Review;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Course>> recommended = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, Float>> ratingMap = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, String>> instructorMap = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Course>> getRecommended() { return recommended; }
    public LiveData<Map<Integer, Float>> getRatingMap() { return ratingMap; }
    public LiveData<Map<Integer, String>> getInstructorMap() { return instructorMap; }

    public void load() {
        Context context = getApplication();
        CourseDao courseDao = new CourseDao(context);
        ReviewDao reviewDao = new ReviewDao(context);
        List<Course> all = courseDao.getAllCourses();
        Map<Integer, Float> ratings = new HashMap<>();
        Map<Integer, String> instructors = new HashMap<>();
        for (Course c : all) {
            List<Review> reviews = reviewDao.getReviews(c.getCourseId());
            float avg = 0f;
            if (!reviews.isEmpty()) {
                int sum = 0;
                for (Review r : reviews) sum += Math.max(0, r.getRating());
                avg = sum / (float) reviews.size();
            }
            ratings.put(c.getCourseId(), avg);
            String name = "官方讲师";
            instructors.put(c.getCourseId(), name);
        }
        List<Course> rec = new ArrayList<>(all);
        rec.sort((a, b) -> Float.compare(ratings.getOrDefault(b.getCourseId(), 0f), ratings.getOrDefault(a.getCourseId(), 0f)));
        recommended.postValue(rec);
        ratingMap.postValue(ratings);
        instructorMap.postValue(instructors);
    }
}
