package com.example.catstudy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.Course;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
    private final DBHelper dbHelper;

    public CartDao(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public void addToCart(int userId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CART, null,
                "user_id=? AND course_id=?",
                new String[]{String.valueOf(userId), String.valueOf(courseId)},
                null, null, null);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        if (!exists) {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("course_id", courseId);
            values.put("create_time", System.currentTimeMillis());
            db.insert(DBHelper.TABLE_CART, null, values);
        }
        db.close();
    }

    public void removeFromCart(int cartId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_CART, "id=?", new String[]{String.valueOf(cartId)});
        db.close();
    }

    public void removeCourseFromCart(int userId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_CART, "user_id=? AND course_id=?", 
                new String[]{String.valueOf(userId), String.valueOf(courseId)});
        db.close();
    }

    public List<Course> getCartItems(int userId) {
        List<Course> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT c.* FROM " + DBHelper.TABLE_COURSE + " c " +
                "INNER JOIN " + DBHelper.TABLE_CART + " t ON c." + DBHelper.COL_COURSE_ID + "=t.course_id " +
                "WHERE t.user_id=? ORDER BY t.create_time DESC";
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
                int priceIdx = cursor.getColumnIndex(DBHelper.COL_PRICE);
                if (priceIdx != -1) {
                    course.setPrice(cursor.getInt(priceIdx));
                }
                list.add(course);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_CART, "user_id=?", new String[]{String.valueOf(userId)});
        db.close();
    }
}
