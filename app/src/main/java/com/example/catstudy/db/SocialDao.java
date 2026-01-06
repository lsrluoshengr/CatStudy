package com.example.catstudy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.Comment;
import com.example.catstudy.model.Post;
import java.util.ArrayList;
import java.util.List;

public class SocialDao {
    private DBHelper dbHelper;

    public SocialDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean hasLiked(int userId, int postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_POST_LIKE, null,
                DBHelper.COL_POST_LIKE_USER_ID + "=? AND " + DBHelper.COL_POST_LIKE_POST_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(postId)}, null, null, null);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        return exists;
    }

    public int getLikeCount(int postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + DBHelper.TABLE_POST_LIKE + " WHERE " + DBHelper.COL_POST_LIKE_POST_ID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(postId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public void toggleLike(int userId, int postId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (hasLiked(userId, postId)) {
            db.delete(DBHelper.TABLE_POST_LIKE,
                    DBHelper.COL_POST_LIKE_USER_ID + "=? AND " + DBHelper.COL_POST_LIKE_POST_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(postId)});
        } else {
            String sql = "INSERT INTO " + DBHelper.TABLE_POST_LIKE + " (" +
                    DBHelper.COL_POST_LIKE_POST_ID + ", " +
                    DBHelper.COL_POST_LIKE_USER_ID + ", " +
                    DBHelper.COL_POST_LIKE_CREATE_TIME + ") VALUES (?, ?, ?)";
            db.execSQL(sql, new Object[]{postId, userId, System.currentTimeMillis()});
        }
    }

    public void addComment(int userId, int postId, String content) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_POST_COMMENT + " (" +
                DBHelper.COL_POST_COMMENT_POST_ID + ", " +
                DBHelper.COL_POST_COMMENT_USER_ID + ", " +
                DBHelper.COL_POST_COMMENT_CONTENT + ", " +
                DBHelper.COL_POST_COMMENT_CREATE_TIME + ") VALUES (?, ?, ?, ?)";
        db.execSQL(sql, new Object[]{postId, userId, content, System.currentTimeMillis()});
    }

    public List<Comment> getComments(int postId) {
        List<Comment> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_POST_COMMENT, null,
                DBHelper.COL_POST_COMMENT_POST_ID + "=?",
                new String[]{String.valueOf(postId)}, null, null,
                DBHelper.COL_POST_COMMENT_CREATE_TIME + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Comment c = new Comment();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_COMMENT_ID)));
                c.setPostId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_COMMENT_POST_ID)));
                c.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_COMMENT_USER_ID)));
                c.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_COMMENT_CONTENT)));
                c.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_COMMENT_CREATE_TIME)));
                list.add(c);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int getCommentCount(int postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + DBHelper.TABLE_POST_COMMENT + " WHERE " + DBHelper.COL_POST_COMMENT_POST_ID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(postId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public void insertPost(Post post) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_POST + " (" +
                DBHelper.COL_POST_USER_ID + ", " +
                DBHelper.COL_POST_CONTENT + ", " +
                DBHelper.COL_POST_IMAGE_URL + ", " +
                DBHelper.COL_POST_CREATE_TIME + ") VALUES (?, ?, ?, ?)";
        db.execSQL(sql, new Object[]{
                post.getUserId(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreateTime()
        });
    }

    public List<Post> getAllPosts() {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_POST, null, null, null, null, null, DBHelper.COL_POST_CREATE_TIME + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Post p = new Post();
                p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_ID)));
                p.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_USER_ID)));
                p.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_CONTENT)));
                p.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_IMAGE_URL)));
                p.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_CREATE_TIME)));
                list.add(p);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Post> getUserPosts(int userId) {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_POST, null,
                DBHelper.COL_POST_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                DBHelper.COL_POST_CREATE_TIME + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Post p = new Post();
                p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_ID)));
                p.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_USER_ID)));
                p.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_CONTENT)));
                p.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_IMAGE_URL)));
                p.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_POST_CREATE_TIME)));
                list.add(p);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
