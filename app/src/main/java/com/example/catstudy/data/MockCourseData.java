package com.example.catstudy.data;

import java.util.ArrayList;
import java.util.List;
import com.example.catstudy.model.Course;

/**
 * 模拟课程数据配置类
 *
 * 字段说明（对应 Course JavaBean）：
 * - courseId：课程唯一标识，整型。管理员可自定义但需唯一
 * - title：课程名称（等同于 courseName），字符串
 * - coverUrl：封面图片地址（支持网络 URL、android.resource、file:// 或 content://）
 * - videoUrl：课程预览或教学视频地址
 * - progress：学习进度，0-100 的整数
 * - category：课程分类（如 “护理”、“内科”等）
 * - price：课程价格，单位积分
 *
 * 修改方式：
 * - 直接在 getMockCourses() 中增删或调整条目即可，不影响其它程序逻辑
 */
public final class MockCourseData {
    private MockCourseData() {}

    /**
     * 返回模拟课程数据（至少 10 条）
     */
    public static List<Course> getMockCourses() {
        List<Course> list = new ArrayList<>();
        String video = "";
        // Local/Server images will be synced by MainActivity
        String cover1 = ""; 
        String cover2 = "";
        String cover3 = "";

        list.add(create(1001, "护理科研", cover1, video, 0,  "护理", 10, 0));
        list.add(create(1002, "内科护理学", cover2, video, 0,  "护理", 10, 0));
        list.add(create(1003, "护理伦理与法规", cover3, video, 0,  "护理", 10, 120));
        list.add(create(1004, "传染病护理", cover1, video, 10,  "护理", 20, 0));
        list.add(create(1005, "内科护理学", cover2, video, 0,  "护理", 10, 50));
        list.add(create(1006, "护理学导论", cover3, video, 0,  "护理", 15, 0));
        list.add(create(1007, "基础护理学", cover1, video, 0,  "护理", 10, 30));
        list.add(create(1008, "外科护理学", cover2, video, 0,  "护理", 20, 0));
        list.add(create(1009, "护理研究",   cover3, video, 0,  "护理", 10, 10));
        list.add(create(1010, "急危重症护理", cover1, video, 0, "护理", 10, 0));
        list.add(create(1011, "儿科护理学", cover2, video, 0, "麻醉", 30, 0));
        list.add(create(1012, "社区护理学", cover3, video, 0, "康复", 25, 8));

        return list;
    }

    private static Course create(int id, String title, String cover, String video, int progress, String category, int price, int enrollmentCount) {
        Course c = new Course();
        c.setCourseId(id);
        c.setTitle(title);
        c.setCoverUrl(cover);
        c.setVideoUrl(video);
        c.setProgress(progress);
        c.setCategory(category);
        c.setPrice(price);
        c.setEnrollmentCount(enrollmentCount);
        return c;
    }
}

