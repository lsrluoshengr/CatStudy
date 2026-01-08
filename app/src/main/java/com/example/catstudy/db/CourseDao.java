package com.example.catstudy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.Course;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    private DBHelper dbHelper;

    public CourseDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void addCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_TITLE, course.getTitle());
        values.put(DBHelper.COL_COVER_URL, course.getCoverUrl());
        values.put(DBHelper.COL_VIDEO_URL, course.getVideoUrl());
        values.put(DBHelper.COL_PROGRESS, course.getProgress());
        values.put(DBHelper.COL_CATEGORY, course.getCategory());
        values.put(DBHelper.COL_PRICE, course.getPrice());
        values.put(DBHelper.COL_ENROLLMENT_COUNT, course.getEnrollmentCount());
        db.insert(DBHelper.TABLE_COURSE, null, values);
        db.close();
    }

    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_COURSE, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COURSE_ID)));
                course.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TITLE)));
                course.setCoverUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_COVER_URL)));
                course.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VIDEO_URL)));
                course.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PROGRESS)));
                course.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORY)));
                int priceIdx = cursor.getColumnIndex(DBHelper.COL_PRICE);
                if (priceIdx != -1) {
                    course.setPrice(cursor.getInt(priceIdx));
                }
                int enrollmentIdx = cursor.getColumnIndex(DBHelper.COL_ENROLLMENT_COUNT);
                if (enrollmentIdx != -1) {
                    course.setEnrollmentCount(cursor.getInt(enrollmentIdx));
                }
                courseList.add(course);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return courseList;
    }

    public void updateAllCourseCovers(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;
        
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // Start transaction for performance and consistency
        try {
            Cursor cursor = db.query(DBHelper.TABLE_COURSE, new String[]{DBHelper.COL_COURSE_ID}, null, null, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                int index = 0;
                do {
                    int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COURSE_ID));
                    String imageUrl = imageUrls.get(index % imageUrls.size());
                    
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.COL_COVER_URL, imageUrl);
                    db.update(DBHelper.TABLE_COURSE, values, DBHelper.COL_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
                    
                    index++;
                } while (cursor.moveToNext());
                cursor.close();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void updateProgress(int courseId, int progress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_PROGRESS, progress);
        db.update(DBHelper.TABLE_COURSE, values, DBHelper.COL_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        db.close();
    }

    public Course getCourse(int courseId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_COURSE, null,
                DBHelper.COL_COURSE_ID + "=?",
                new String[]{String.valueOf(courseId)}, null, null, null);

        Course course = null;
        if (cursor != null && cursor.moveToFirst()) {
            course = new Course();
            course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COURSE_ID)));
            course.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TITLE)));
            course.setCoverUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_COVER_URL)));
                course.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VIDEO_URL)));
                course.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PROGRESS)));
                course.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORY)));
                int priceIdx = cursor.getColumnIndex(DBHelper.COL_PRICE);
                if (priceIdx != -1) {
                    course.setPrice(cursor.getInt(priceIdx));
                }
                int enrollmentIdx = cursor.getColumnIndex(DBHelper.COL_ENROLLMENT_COUNT);
                if (enrollmentIdx != -1) {
                    course.setEnrollmentCount(cursor.getInt(enrollmentIdx));
                }
                cursor.close();
        }
        db.close();
        return course;
    }

    public List<Course> searchCourses(String keyword) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // SELECT * FROM table_course WHERE title LIKE %keyword%
        String selection = DBHelper.COL_TITLE + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + keyword + "%"};
        
        Cursor cursor = db.query(DBHelper.TABLE_COURSE, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COURSE_ID)));
                course.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TITLE)));
                course.setCoverUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_COVER_URL)));
                course.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VIDEO_URL)));
                course.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PROGRESS)));
                course.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORY)));
                int priceIdx = cursor.getColumnIndex(DBHelper.COL_PRICE);
                if (priceIdx != -1) {
                    course.setPrice(cursor.getInt(priceIdx));
                }
                int enrollmentIdx = cursor.getColumnIndex(DBHelper.COL_ENROLLMENT_COUNT);
                if (enrollmentIdx != -1) {
                    course.setEnrollmentCount(cursor.getInt(enrollmentIdx));
                }
                courseList.add(course);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return courseList;
    }
}
