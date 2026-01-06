package com.example.catstudy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.User;

public class UserDao {
    private DBHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long register(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USERNAME, user.getUsername());
        values.put(DBHelper.COL_PASSWORD, user.getPassword());
        values.put(DBHelper.COL_NICKNAME, user.getNickname());
        values.put(DBHelper.COL_AVATAR_URL, user.getAvatarUrl());
        values.put(DBHelper.COL_COINS, user.getCoins());
        long id = db.insert(DBHelper.TABLE_USER, null, values);
        db.close();
        return id;
    }

    public User login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USER, null,
                DBHelper.COL_USERNAME + "=? AND " + DBHelper.COL_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PASSWORD)));
            user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NICKNAME)));
            user.setAvatarUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_AVATAR_URL)));
            user.setCoins(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COINS)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_GENDER)));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_AGE)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PHONE)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EMAIL)));
            user.setSignature(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_SIGNATURE)));
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USER, new String[]{DBHelper.COL_USER_ID},
                DBHelper.COL_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public User getUser(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USER, null,
                DBHelper.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PASSWORD)));
            user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NICKNAME)));
            user.setAvatarUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_AVATAR_URL)));
            user.setCoins(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_COINS)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_GENDER)));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_AGE)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PHONE)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EMAIL)));
            user.setSignature(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_SIGNATURE)));
            cursor.close();
        }
        db.close();
        return user;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_NICKNAME, user.getNickname());
        values.put(DBHelper.COL_AVATAR_URL, user.getAvatarUrl());
        values.put(DBHelper.COL_GENDER, user.getGender());
        values.put(DBHelper.COL_AGE, user.getAge());
        values.put(DBHelper.COL_PHONE, user.getPhone());
        values.put(DBHelper.COL_EMAIL, user.getEmail());
        values.put(DBHelper.COL_SIGNATURE, user.getSignature());
        db.update(DBHelper.TABLE_USER, values, DBHelper.COL_USER_ID + "=?", new String[]{String.valueOf(user.getUserId())});
        db.close();
    }

    public void updateCoins(int userId, int coins) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_COINS, coins);
        db.update(DBHelper.TABLE_USER, values, DBHelper.COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }
}
