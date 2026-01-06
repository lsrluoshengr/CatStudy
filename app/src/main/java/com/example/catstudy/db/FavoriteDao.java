package com.example.catstudy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.Course;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDao {
    private DBHelper dbHelper;

    public FavoriteDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean isFavorited(int userId, int courseId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_FAVORITE, null,
                DBHelper.COL_FAV_USER_ID + "=? AND " + DBHelper.COL_FAV_COURSE_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(courseId)}, null, null, null);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        return exists;
    }

    public void toggleFavorite(int userId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (isFavorited(userId, courseId)) {
            db.delete(DBHelper.TABLE_FAVORITE,
                    DBHelper.COL_FAV_USER_ID + "=? AND " + DBHelper.COL_FAV_COURSE_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(courseId)});
        } else {
            String sql = "INSERT INTO " + DBHelper.TABLE_FAVORITE + " (" +
                    DBHelper.COL_FAV_USER_ID + ", " + DBHelper.COL_FAV_COURSE_ID + ", " + DBHelper.COL_FAV_CREATE_TIME +
                    ") VALUES (?, ?, ?)";
            db.execSQL(sql, new Object[]{userId, courseId, System.currentTimeMillis()});
        }
    }

    public List<Course> getMyFavorites(int userId) {
        List<Course> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT c.* FROM " + DBHelper.TABLE_COURSE + " c " +
                "INNER JOIN " + DBHelper.TABLE_FAVORITE + " f ON c." + DBHelper.COL_COURSE_ID + "=f." + DBHelper.COL_FAV_COURSE_ID + " " +
                "WHERE f." + DBHelper.COL_FAV_USER_ID + "=? ORDER BY f." + DBHelper.COL_FAV_CREATE_TIME + " DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COURSE_ID)));
                course.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TITLE)));
                course.setCoverUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_COVER_URL)));
                course.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VIDEO_URL)));
                course.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PROGRESS)));
                course.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORY)));
                list.add(course);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
