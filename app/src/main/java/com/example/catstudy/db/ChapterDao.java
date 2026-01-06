package com.example.catstudy.db;

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
