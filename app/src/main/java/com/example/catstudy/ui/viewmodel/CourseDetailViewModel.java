package com.example.catstudy.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.catstudy.db.ChapterDao;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.db.ReviewDao;
import com.example.catstudy.model.Chapter;
import com.example.catstudy.model.Course;
import com.example.catstudy.model.Review;
import java.util.List;

public class CourseDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<Course> course = new MutableLiveData<>();
    private final MutableLiveData<Float> ratingAvg = new MutableLiveData<>();
    private final MutableLiveData<List<Chapter>> chapters = new MutableLiveData<>();

    public CourseDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Course> getCourse() { return course; }
    public LiveData<Float> getRatingAvg() { return ratingAvg; }
    public LiveData<List<Chapter>> getChapters() { return chapters; }

    public void load(int courseId) {
        Context context = getApplication();
        CourseDao courseDao = new CourseDao(context);
        ReviewDao reviewDao = new ReviewDao(context);
        ChapterDao chapterDao = new ChapterDao(context);
        Course c = courseDao.getCourse(courseId);
        List<Review> rs = reviewDao.getReviews(courseId);
        float avg = 0f;
        if (!rs.isEmpty()) {
            int sum = 0;
            for (Review r : rs) sum += Math.max(0, r.getRating());
            avg = sum / (float) rs.size();
        }
        List<Chapter> ch = chapterDao.getChaptersByCourseId(courseId);
        course.postValue(c);
        ratingAvg.postValue(avg);
        chapters.postValue(ch);
    }
}
