package com.example.catstudy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CheckInDao {
    private DBHelper dbHelper;

    public CheckInDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean checkIn(int userId, String date, int points) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Check if already checked in
            if (isCheckedInInternal(db, userId, date)) {
                return false;
            }

            // Calculate consecutive days
            int consecutiveDays = 1;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String yesterday = sdf.format(calendar.getTime());

            Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, new String[]{"consecutive_days"},
                    "user_id=? AND checkin_date=?",
                    new String[]{String.valueOf(userId), yesterday}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    consecutiveDays = cursor.getInt(0) + 1;
                }
                cursor.close();
            }

            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("checkin_date", date);
            values.put("points", points);
            values.put("consecutive_days", consecutiveDays);
            
            long result = db.insert(DBHelper.TABLE_CHECKIN, null, values);
            if (result != -1) {
                // Update user coins
                db.execSQL("UPDATE " + DBHelper.TABLE_USER + " SET " + DBHelper.COL_COINS + " = " + DBHelper.COL_COINS + " + ? WHERE " + DBHelper.COL_USER_ID + " = ?", new Object[]{points, userId});
                db.setTransactionSuccessful();
                return true;
            }
            android.util.Log.e("CheckInDao", "Insert failed: result=" + result);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("CheckInDao", "CheckIn error: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private boolean isCheckedInInternal(SQLiteDatabase db, int userId, String date) {
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, null,
                "user_id=? AND checkin_date=?",
                new String[]{String.valueOf(userId), date}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public int getConsecutiveDays(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, new String[]{"consecutive_days"},
                "user_id=?", new String[]{String.valueOf(userId)}, 
                null, null, "checkin_date DESC", "1");
        
        int days = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Check if the last check-in was today or yesterday. If older, chain is broken (but we return the stored value? No, return 0 if broken).
                // Actually, if last checkin was today, return its consecutive_days.
                // If last checkin was yesterday, return its consecutive_days.
                // If older, return 0.
                
                String dateStr = ""; // Need to select date too
            }
            cursor.close();
        }
        // Let's re-query with date
        cursor = db.query(DBHelper.TABLE_CHECKIN, new String[]{"checkin_date", "consecutive_days"},
                "user_id=?", new String[]{String.valueOf(userId)}, 
                null, null, "checkin_date DESC", "1");
                
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String lastDate = cursor.getString(0);
                int count = cursor.getInt(1);
                
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date last = sdf.parse(lastDate);
                    Date today = new Date();
                    String todayStr = sdf.format(today);
                    
                    if (lastDate.equals(todayStr)) {
                        days = count;
                    } else {
                        // Check if yesterday
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(today);
                        cal.add(Calendar.DAY_OF_YEAR, -1);
                        String yesterday = sdf.format(cal.getTime());
                        if (lastDate.equals(yesterday)) {
                            days = count;
                        } else {
                            days = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        db.close();
        return days;
    }

    public boolean isCheckedIn(int userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = isCheckedInInternal(db, userId, date);
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
