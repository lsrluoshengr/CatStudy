package com.example.catstudy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.Review;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao {
    private DBHelper dbHelper;

    public ReviewDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void addReview(Review review) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_REVIEW + " (" +
                DBHelper.COL_REVIEW_USER_ID + ", " +
                DBHelper.COL_REVIEW_COURSE_ID + ", " +
                DBHelper.COL_REVIEW_CONTENT + ", " +
                DBHelper.COL_REVIEW_RATING + ", " +
                DBHelper.COL_REVIEW_CREATE_TIME + ") VALUES (?, ?, ?, ?, ?)";
        db.execSQL(sql, new Object[]{
                review.getUserId(),
                review.getCourseId(),
                review.getContent(),
                review.getRating(),
                review.getCreateTime()
        });
        db.close();
    }

    public List<Review> getReviews(int courseId) {
        List<Review> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_REVIEW, null,
                DBHelper.COL_REVIEW_COURSE_ID + "=?",
                new String[]{String.valueOf(courseId)}, null, null,
                DBHelper.COL_REVIEW_CREATE_TIME + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Review r = new Review();
                r.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_REVIEW_ID)));
                r.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_REVIEW_USER_ID)));
                r.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_REVIEW_COURSE_ID)));
                r.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_REVIEW_CONTENT)));
                r.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_REVIEW_RATING)));
                r.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_REVIEW_CREATE_TIME)));
                list.add(r);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }
}
