package com.example.catstudy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.Course;
import com.example.catstudy.model.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private final DBHelper dbHelper;

    public OrderDao(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public long addOrder(int userId, int totalCoins, List<Course> courses) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        long orderId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("total_coins", totalCoins);
            values.put("create_time", System.currentTimeMillis());
            orderId = db.insert(DBHelper.TABLE_ORDER, null, values);

            if (orderId != -1 && courses != null) {
                for (Course course : courses) {
                    ContentValues itemValues = new ContentValues();
                    itemValues.put("order_id", orderId);
                    itemValues.put("course_id", course.getCourseId());
                    itemValues.put("price", course.getPrice());
                    db.insert(DBHelper.TABLE_ORDER_ITEM, null, itemValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return orderId;
    }

    public long addOrder(int userId, int totalCoins) {
        return addOrder(userId, totalCoins, null);
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(DBHelper.TABLE_ORDER, null, "user_id=?", 
                new String[]{String.valueOf(userId)}, null, null, "create_time DESC");
        
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    int totalCoins = cursor.getInt(cursor.getColumnIndex("total_coins"));
                    long createTime = cursor.getLong(cursor.getColumnIndex("create_time"));
                    
                    Order order = new Order(id, userId, totalCoins, createTime);
                    order.setCourseList(getOrderItems(db, id));
                    orders.add(order);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return orders;
    }

    private List<Course> getOrderItems(SQLiteDatabase db, int orderId) {
        List<Course> courses = new ArrayList<>();
        // Join TABLE_ORDER_ITEM and TABLE_COURSE
        String query = "SELECT c.* FROM " + DBHelper.TABLE_COURSE + " c " +
                "INNER JOIN " + DBHelper.TABLE_ORDER_ITEM + " oi ON c." + DBHelper.COL_COURSE_ID + " = oi.course_id " +
                "WHERE oi.order_id = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Course course = new Course();
                    course.setCourseId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_COURSE_ID)));
                    course.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE)));
                    course.setCoverUrl(cursor.getString(cursor.getColumnIndex(DBHelper.COL_COVER_URL)));
                    course.setVideoUrl(cursor.getString(cursor.getColumnIndex(DBHelper.COL_VIDEO_URL)));
                    course.setProgress(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PROGRESS)));
                    course.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COL_CATEGORY)));
                    course.setPrice(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PRICE)));
                    courses.add(course);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return courses;
    }
}
