package com.example.catstudy.util;

import android.content.Context;
import com.example.catstudy.R;

public class CourseCoverUtils {
    
    // 目前 mipmap 中有 course1 到 course13
    private static final int MAX_COURSE_IMAGES = 13;

    public static int getCoverResId(Context context, int courseId) {
        if (context == null) return R.mipmap.ic_launcher;
        
        // 1. 优先尝试特定 ID 的封面: course_cover_{id}
        String specificName = "course_cover_" + courseId;
        int specificId = getResId(context, specificName);
        if (specificId != 0) return specificId;

        // 2. 映射到通用的 course{1-13} 图片
        // 使用取模运算实现循环映射
        int index = (Math.abs(courseId) % MAX_COURSE_IMAGES) + 1;
        String genericName = "course" + index;
        int genericId = getResId(context, genericName);

        // 3. 如果映射的图片不存在，尝试使用 course1 作为默认
        if (genericId == 0) {
             genericId = getResId(context, "course1");
        }

        // 4. 最终兜底
        return genericId != 0 ? genericId : R.mipmap.ic_launcher;
    }

    private static int getResId(Context context, String name) {
        int resId = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
        if (resId == 0) {
            resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        }
        return resId;
    }
}
