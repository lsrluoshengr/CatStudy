package com.example.catstudy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.catstudy.model.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterDao {
    private DBHelper dbHelper;

    public ChapterDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void updateAllChapterVideos(List<String> videoUrls) {
        if (videoUrls == null || videoUrls.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Get all chapter IDs
            Cursor cursor = db.query(DBHelper.TABLE_CHAPTER, new String[]{DBHelper.COL_CHAPTER_ID}, null, null, null, null, null);
            List<Integer> ids = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ids.add(cursor.getInt(0));
                }
                cursor.close();
            }

            // Update each
            for (int i = 0; i < ids.size(); i++) {
                String url = videoUrls.get(i % videoUrls.size());
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_CHAPTER_VIDEO_URL, url);
                db.update(DBHelper.TABLE_CHAPTER, values, DBHelper.COL_CHAPTER_ID + "=?", new String[]{String.valueOf(ids.get(i))});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Chapter> getChaptersByCourseId(int courseId) {
        List<Chapter> chapterList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DBHelper.COL_CHAPTER_COURSE_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(courseId)};
        String orderBy = DBHelper.COL_CHAPTER_SORT_ORDER + " ASC";
        
        Cursor cursor = db.query(DBHelper.TABLE_CHAPTER, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Chapter chapter = new Chapter();
                chapter.setChapterId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CHAPTER_ID)));
                chapter.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CHAPTER_COURSE_ID)));
                chapter.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CHAPTER_TITLE)));
                chapter.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CHAPTER_VIDEO_URL)));
                chapter.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_CHAPTER_DURATION)));
                chapter.setSortOrder(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CHAPTER_SORT_ORDER)));
                
                chapterList.add(chapter);
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        db.close();
        return chapterList;
    }

    // You can add more methods here if needed in the future
}
