package com.example.catstudy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.catstudy.data.MockCourseData;
import com.example.catstudy.model.Course;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "learning_mate.db";
    private static final int DATABASE_VERSION = 11;
    private static final String TAG = "DBHelper";



    // Table Names
    public static final String TABLE_USER = "table_user";
    public static final String TABLE_COURSE = "table_course";
    public static final String TABLE_NEWS = "table_news";
    public static final String TABLE_FAVORITE = "table_favorite";
    public static final String TABLE_REVIEW = "table_review";
    public static final String TABLE_POST_LIKE = "table_post_like";
    public static final String TABLE_POST_COMMENT = "table_post_comment";
    public static final String TABLE_POST = "table_post";
    public static final String TABLE_CART = "table_cart";
    public static final String TABLE_ORDER = "table_order";
    public static final String TABLE_ORDER_ITEM = "table_order_item";
    public static final String TABLE_CHAPTER = "table_chapter";
    public static final String TABLE_CHECKIN = "table_checkin";
    public static final String TABLE_SEARCH_HISTORY = "table_search_history";

    // Course Table Columns
    public static final String COL_COURSE_ID = "course_id";
    public static final String COL_TITLE = "title";
    public static final String COL_COVER_URL = "cover_url";
    public static final String COL_VIDEO_URL = "video_url";
    public static final String COL_PROGRESS = "progress";
    public static final String COL_CATEGORY = "category";
    public static final String COL_PRICE = "price";
    public static final String COL_ENROLLMENT_COUNT = "enrollment_count";
    
    // User Table Columns
    public static final String COL_CHAPTER_ID = "chapter_id";
    public static final String COL_CHAPTER_COURSE_ID = "course_id";
    public static final String COL_CHAPTER_TITLE = "title";
    public static final String COL_CHAPTER_VIDEO_URL = "video_url";
    public static final String COL_CHAPTER_DURATION = "duration";
    public static final String COL_CHAPTER_SORT_ORDER = "sort_order";

    // User Table Columns
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_NICKNAME = "nickname";
    public static final String COL_AVATAR_URL = "avatar_url";
    public static final String COL_COINS = "coins";
    // New User Columns
    public static final String COL_GENDER = "gender";
    public static final String COL_AGE = "age";
    public static final String COL_PHONE = "phone";
    public static final String COL_EMAIL = "email";
    public static final String COL_SIGNATURE = "signature";

    // News Table Columns
    public static final String COL_NEWS_ID = "news_id";
    public static final String COL_AUTHOR_NAME = "author_name";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMAGE_URL = "image_url";
    public static final String COL_PUBLISH_TIME = "publish_time";
    // Favorite Table Columns
    public static final String COL_FAV_ID = "id";
    public static final String COL_FAV_USER_ID = "user_id";
    public static final String COL_FAV_COURSE_ID = "course_id";
    public static final String COL_FAV_CREATE_TIME = "create_time";
    // Review Table Columns
    public static final String COL_REVIEW_ID = "id";
    public static final String COL_REVIEW_USER_ID = "user_id";
    public static final String COL_REVIEW_COURSE_ID = "course_id";
    public static final String COL_REVIEW_CONTENT = "content";
    public static final String COL_REVIEW_RATING = "rating";
    public static final String COL_REVIEW_CREATE_TIME = "create_time";
    // Post Like Columns
    public static final String COL_POST_LIKE_ID = "id";
    public static final String COL_POST_LIKE_POST_ID = "post_id";
    public static final String COL_POST_LIKE_USER_ID = "user_id";
    public static final String COL_POST_LIKE_CREATE_TIME = "create_time";
    // Post Comment Columns
    public static final String COL_POST_COMMENT_ID = "id";
    public static final String COL_POST_COMMENT_POST_ID = "post_id";
    public static final String COL_POST_COMMENT_USER_ID = "user_id";
    public static final String COL_POST_COMMENT_CONTENT = "content";
    public static final String COL_POST_COMMENT_CREATE_TIME = "create_time";
    // Post Table Columns
    public static final String COL_POST_ID = "id";
    public static final String COL_POST_USER_ID = "user_id";
    public static final String COL_POST_CONTENT = "content";
    public static final String COL_POST_IMAGE_URL = "image_url";
    public static final String COL_POST_CREATE_TIME = "create_time";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT, " +
                COL_NICKNAME + " TEXT, " +
                COL_AVATAR_URL + " TEXT, " +
                COL_COINS + " INTEGER, " +
                COL_GENDER + " TEXT, " +
                COL_AGE + " INTEGER, " +
                COL_PHONE + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_SIGNATURE + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        // Create Course Table
        String CREATE_COURSE_TABLE = "CREATE TABLE " + TABLE_COURSE + " (" +
                COL_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_COVER_URL + " TEXT, " +
                COL_VIDEO_URL + " TEXT, " +
                COL_PROGRESS + " INTEGER DEFAULT 0, " +
                COL_CATEGORY + " TEXT, " +
                COL_PRICE + " INTEGER DEFAULT 0, " +
                COL_ENROLLMENT_COUNT + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_COURSE_TABLE);

        // Create News Table
        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + " (" +
                COL_NEWS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AUTHOR_NAME + " TEXT, " +
                COL_CONTENT + " TEXT, " +
                COL_IMAGE_URL + " TEXT, " +
                COL_PUBLISH_TIME + " LONG)";
        db.execSQL(CREATE_NEWS_TABLE);

        // Create Favorite Table
        String CREATE_FAVORITE_TABLE = "CREATE TABLE " + TABLE_FAVORITE + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAV_USER_ID + " INTEGER, " +
                COL_FAV_COURSE_ID + " INTEGER, " +
                COL_FAV_CREATE_TIME + " LONG)";
        db.execSQL(CREATE_FAVORITE_TABLE);

        // Create Review Table
        String CREATE_REVIEW_TABLE = "CREATE TABLE " + TABLE_REVIEW + " (" +
                COL_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REVIEW_USER_ID + " INTEGER, " +
                COL_REVIEW_COURSE_ID + " INTEGER, " +
                COL_REVIEW_CONTENT + " TEXT, " +
                COL_REVIEW_RATING + " INTEGER, " +
                COL_REVIEW_CREATE_TIME + " LONG)";
        db.execSQL(CREATE_REVIEW_TABLE);

        String CREATE_POST_LIKE_TABLE = "CREATE TABLE " + TABLE_POST_LIKE + " (" +
                COL_POST_LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POST_LIKE_POST_ID + " INTEGER, " +
                COL_POST_LIKE_USER_ID + " INTEGER, " +
                COL_POST_LIKE_CREATE_TIME + " LONG)";
        db.execSQL(CREATE_POST_LIKE_TABLE);

        String CREATE_POST_COMMENT_TABLE = "CREATE TABLE " + TABLE_POST_COMMENT + " (" +
                COL_POST_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POST_COMMENT_POST_ID + " INTEGER, " +
                COL_POST_COMMENT_USER_ID + " INTEGER, " +
                COL_POST_COMMENT_CONTENT + " TEXT, " +
                COL_POST_COMMENT_CREATE_TIME + " LONG)";
        db.execSQL(CREATE_POST_COMMENT_TABLE);

        String CREATE_POST_TABLE = "CREATE TABLE " + TABLE_POST + " (" +
                COL_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POST_USER_ID + " INTEGER, " +
                COL_POST_CONTENT + " TEXT, " +
                COL_POST_IMAGE_URL + " TEXT, " +
                COL_POST_CREATE_TIME + " LONG)";
        db.execSQL(CREATE_POST_TABLE);

        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "course_id INTEGER, " +
                "create_time LONG)";
        db.execSQL(CREATE_CART_TABLE);

        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "total_coins INTEGER, " +
                "create_time LONG)";
        db.execSQL(CREATE_ORDER_TABLE);

        String CREATE_ORDER_ITEM_TABLE = "CREATE TABLE " + TABLE_ORDER_ITEM + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "course_id INTEGER, " +
                "price INTEGER)";
        db.execSQL(CREATE_ORDER_ITEM_TABLE);

        // Create Chapter Table
        String CREATE_CHAPTER_TABLE = "CREATE TABLE " + TABLE_CHAPTER + " (" +
                COL_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CHAPTER_COURSE_ID + " INTEGER, " +
                COL_CHAPTER_TITLE + " TEXT, " +
                COL_CHAPTER_VIDEO_URL + " TEXT, " +
                COL_CHAPTER_DURATION + " LONG, " +
                COL_CHAPTER_SORT_ORDER + " INTEGER)";
        db.execSQL(CREATE_CHAPTER_TABLE);

        String CREATE_CHECKIN_TABLE = "CREATE TABLE " + TABLE_CHECKIN + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "checkin_date TEXT, " +
                "points INTEGER, " +
                "consecutive_days INTEGER DEFAULT 1)";
        db.execSQL(CREATE_CHECKIN_TABLE);

        String CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE " + TABLE_SEARCH_HISTORY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "keyword TEXT UNIQUE, " +
                "create_time LONG)";
        db.execSQL(CREATE_SEARCH_HISTORY_TABLE);

        initMockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            // Drop old tables and recreate to ensure clean mock data seeding
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_LIKE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_COMMENT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEM);
            
            onCreate(db);
            return;
        }

        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITE + " (" +
                    COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_FAV_USER_ID + " INTEGER, " +
                    COL_FAV_COURSE_ID + " INTEGER, " +
                    COL_FAV_CREATE_TIME + " LONG)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REVIEW + " (" +
                    COL_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_REVIEW_USER_ID + " INTEGER, " +
                    COL_REVIEW_COURSE_ID + " INTEGER, " +
                    COL_REVIEW_CONTENT + " TEXT, " +
                    COL_REVIEW_RATING + " INTEGER, " +
                    COL_REVIEW_CREATE_TIME + " LONG)");
        }
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_POST_LIKE + " (" +
                    COL_POST_LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_POST_LIKE_POST_ID + " INTEGER, " +
                    COL_POST_LIKE_USER_ID + " INTEGER, " +
                    COL_POST_LIKE_CREATE_TIME + " LONG)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_POST_COMMENT + " (" +
                    COL_POST_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_POST_COMMENT_POST_ID + " INTEGER, " +
                    COL_POST_COMMENT_USER_ID + " INTEGER, " +
                    COL_POST_COMMENT_CONTENT + " TEXT, " +
                    COL_POST_COMMENT_CREATE_TIME + " LONG)");
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_POST + " (" +
                    COL_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_POST_USER_ID + " INTEGER, " +
                    COL_POST_CONTENT + " TEXT, " +
                    COL_POST_IMAGE_URL + " TEXT, " +
                    COL_POST_CREATE_TIME + " LONG)");
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_COURSE + " ADD COLUMN " + COL_PRICE + " INTEGER DEFAULT 0");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "course_id INTEGER, " +
                    "create_time LONG)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ORDER + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "total_coins INTEGER, " +
                    "create_time LONG)");
        }
        if (oldVersion < 6) {
            // Create Chapter Table
            String CREATE_CHAPTER_TABLE = "CREATE TABLE " + TABLE_CHAPTER + " (" +
                    COL_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CHAPTER_COURSE_ID + " INTEGER, " +
                    COL_CHAPTER_TITLE + " TEXT, " +
                    COL_CHAPTER_VIDEO_URL + " TEXT, " +
                    COL_CHAPTER_DURATION + " LONG, " +
                    COL_CHAPTER_SORT_ORDER + " INTEGER)";
            db.execSQL(CREATE_CHAPTER_TABLE);
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_GENDER + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_AGE + " INTEGER");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_PHONE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_EMAIL + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COL_SIGNATURE + " TEXT");
        }
        if (oldVersion < 9) {
            String CREATE_ORDER_ITEM_TABLE = "CREATE TABLE " + TABLE_ORDER_ITEM + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER, " +
                    "course_id INTEGER, " +
                    "price INTEGER)";
            db.execSQL(CREATE_ORDER_ITEM_TABLE);
        }
        if (oldVersion < 10) {
            String CREATE_CHECKIN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CHECKIN + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "checkin_date TEXT, " +
                    "points INTEGER)";
            db.execSQL(CREATE_CHECKIN_TABLE);

            String CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SEARCH_HISTORY + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "keyword TEXT UNIQUE, " +
                    "create_time LONG)";
            db.execSQL(CREATE_SEARCH_HISTORY_TABLE);
        }
        if (oldVersion < 11) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_CHECKIN + " ADD COLUMN consecutive_days INTEGER DEFAULT 1");
            } catch (Exception e) {
                // Ignore if already exists
            }
        }
    }

    private void initMockData(SQLiteDatabase db) {
        // Init Courses from MockCourseData
        List<Course> mockCourses = MockCourseData.getMockCourses();
        for (Course course : mockCourses) {
            String insertCourseSql = "INSERT INTO " + TABLE_COURSE + " (" +
                    COL_COURSE_ID + ", " + COL_TITLE + ", " + COL_COVER_URL + ", " + COL_VIDEO_URL + ", " + 
                    COL_PROGRESS + ", " + COL_CATEGORY + ", " + COL_PRICE + ", " + COL_ENROLLMENT_COUNT +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            // We use the ID from MockCourseData to ensure consistency
            db.execSQL(insertCourseSql, new Object[]{
                course.getCourseId(),
                course.getTitle(), 
                course.getCoverUrl(), 
                course.getVideoUrl(), 
                course.getProgress(), 
                course.getCategory(), 
                course.getPrice(),
                course.getEnrollmentCount()
            });
            
            // Generate mock chapters for this course
            int chapterCount = 3 + (int) (Math.random() * 8);
            for (int j = 0; j < chapterCount; j++) {
                int chapterNum = j + 1;
                String chapterTitle = "第" + chapterNum + "章";
                long duration = 600000 + (long) (Math.random() * 1200000); // 10-30 minutes
                int sortOrder = chapterNum;
                
                String insertChapterSql = "INSERT INTO " + TABLE_CHAPTER + " (" +
                        COL_CHAPTER_COURSE_ID + ", " + COL_CHAPTER_TITLE + ", " + 
                        COL_CHAPTER_VIDEO_URL + ", " + COL_CHAPTER_DURATION + ", " + 
                        COL_CHAPTER_SORT_ORDER + ") VALUES (?, ?, ?, ?, ?)";
                
                // Initialize with empty video URL, to be populated by API sync
                String chapterVideo = "";
                db.execSQL(insertChapterSql, new Object[]{
                    course.getCourseId(), // Use the same course ID
                    chapterTitle, 
                    chapterVideo, 
                    duration, 
                    sortOrder
                });
            }
        }
        
        initMockUsers(db);
        initMockPosts(db);
        initMockReviews(db);
    }

    private void initMockUsers(SQLiteDatabase db) {
        String[] names = {"Alice", "Bob", "Charlie", "David", "Eve"};
        String[] avatars = {
            "https://api.dicebear.com/7.x/avataaars/png?seed=Alice",
            "https://api.dicebear.com/7.x/avataaars/png?seed=Bob",
            "https://api.dicebear.com/7.x/avataaars/png?seed=Charlie",
            "https://api.dicebear.com/7.x/avataaars/png?seed=David",
            "https://api.dicebear.com/7.x/avataaars/png?seed=Eve"
        };
        
        for (int i = 0; i < names.length; i++) {
            String sql = "INSERT INTO " + TABLE_USER + " (" +
                    COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_NICKNAME + ", " + COL_AVATAR_URL + ", " + COL_COINS +
                    ") VALUES (?, ?, ?, ?, ?)";
            db.execSQL(sql, new Object[]{"user" + (i+1), "123456", names[i], avatars[i], 100 + i * 10});
        }
    }

    private void initMockPosts(SQLiteDatabase db) {
        String[] contents = {
            "刚刚学习了心肺复苏术，感觉非常实用！#急救 #医疗",
            "中医的基础理论真是博大精深，阴阳五行很有意思。",
            "今天的内科护理课笔记，分享给大家~ [图片]",
            "求助：口腔正畸的矫正器种类有哪些优缺点？",
            "打卡第10天，坚持学习！",
            "推荐大家看《急诊室故事》，很真实。",
            "考前复习，压力山大，大家一起加油！",
            "这个APP的课程质量不错，老师讲得很细致。"
        };
        String[] images = {
            "https://img.zcool.cn/community/01d90d5764f62f0000018c1b650222.jpg@1280w_1l_2o_100sh.jpg",
            "",
            "https://img.zcool.cn/community/01f09e577b85450000018c1b4f4c2e.jpg@1280w_1l_2o_100sh.jpg",
            "",
            "https://img.zcool.cn/community/019c29577b85470000012e7e78553f.jpg@1280w_1l_2o_100sh.jpg",
            "",
            "",
            "https://img.zcool.cn/community/010a1b554c01d1000001bf72a68b37.jpg@1280w_1l_2o_100sh.jpg"
        };
        
        // We assume users 1-5 exist
        for (int i = 0; i < contents.length; i++) {
            int userId = (i % 5) + 1; 
            String sql = "INSERT INTO " + TABLE_POST + " (" +
                    COL_POST_USER_ID + ", " + COL_POST_CONTENT + ", " + COL_POST_IMAGE_URL + ", " + COL_POST_CREATE_TIME +
                    ") VALUES (?, ?, ?, ?)";
            db.execSQL(sql, new Object[]{userId, contents[i], images[i], System.currentTimeMillis() - i * 3600000});
        }
    }
    
    private void initMockReviews(SQLiteDatabase db) {
        String[] reviewContents = {
            "课程非常棒，通俗易懂！",
            "老师讲得很好，受益匪浅。",
            "希望能多一些实操演示。",
            "内容很详细，适合初学者。",
            "声音有点小，希望改进。"
        };
        
        // Add reviews for first few courses
        for (int courseId = 1; courseId <= 5; courseId++) {
            for (int i = 0; i < 3; i++) {
                int userId = (i % 5) + 1;
                int rating = 3 + (int)(Math.random() * 3); // 3-5 stars
                String sql = "INSERT INTO " + TABLE_REVIEW + " (" +
                        COL_REVIEW_USER_ID + ", " + COL_REVIEW_COURSE_ID + ", " + COL_REVIEW_CONTENT + ", " + COL_REVIEW_RATING + ", " + COL_REVIEW_CREATE_TIME +
                        ") VALUES (?, ?, ?, ?, ?)";
                db.execSQL(sql, new Object[]{userId, courseId, reviewContents[i % reviewContents.length], rating, System.currentTimeMillis() - i * 86400000});
            }
        }
    }
}
