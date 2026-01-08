package com.example.catstudy.util;

import android.content.Context;
import com.example.catstudy.R;

public class CourseCoverUtils {
    public static int getCoverResId(Context context, int courseId) {
        // User requested to remove local mapping and only use API images.
        // Returning a default placeholder here.
        return R.mipmap.ic_launcher;
    }
}
