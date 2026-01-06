package com.example.catstudy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class CheckInDao {
    private DBHelper dbHelper;

    public CheckInDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean checkIn(int userId, String date, int points) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Check if already checked in
        if (isCheckedIn(userId, date)) {
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("checkin_date", date);
        values.put("points", points);
        
        long result = db.insert(DBHelper.TABLE_CHECKIN, null, values);
        db.close();
        return result != -1;
    }

    public boolean isCheckedIn(int userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, null,
                "user_id=? AND checkin_date=?",
                new String[]{String.valueOf(userId), date}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public List<String> getCheckInDates(int userId) {
        List<String> dates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, new String[]{"checkin_date"},
                "user_id=?", new String[]{String.valueOf(userId)}, 
                null, null, "checkin_date DESC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                dates.add(cursor.getString(0));
            }
            cursor.close();
        }
        db.close();
        return dates;
    }
}
