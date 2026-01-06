package com.example.catstudy;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.catstudy.db.CartDao;
import com.example.catstudy.db.CourseDao;
import com.example.catstudy.db.OrderDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PurchaseFlowTest {
    private Context context;
    private CartDao cartDao;
    private OrderDao orderDao;
    private CourseDao courseDao;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cartDao = new CartDao(context);
        orderDao = new OrderDao(context);
        courseDao = new CourseDao(context);
    }

    @Test
    public void testPurchaseFlow() {
        // 1. Setup Data
        int userId = 1001; // Test User ID
        
        // Ensure at least one course exists
        List<Course> allCourses = courseDao.getAllCourses();
        if (allCourses.isEmpty()) {
            Course newCourse = new Course();
            newCourse.setTitle("Test Course");
            newCourse.setPrice(100);
            newCourse.setCategory("Test");
            courseDao.addCourse(newCourse);
            allCourses = courseDao.getAllCourses();
        }
        assertTrue("Should have courses", !allCourses.isEmpty());
        Course testCourse = allCourses.get(0);
        int courseId = testCourse.getCourseId();

        // Clear previous state for this user/course
        cartDao.removeCourseFromCart(userId, courseId);
        
        // 2. Add to Cart
        cartDao.addToCart(userId, courseId);
        
        // Verify in Cart
        List<Course> cartItems = cartDao.getCartItems(userId);
        boolean inCart = false;
        for (Course c : cartItems) {
            if (c.getCourseId() == courseId) {
                inCart = true;
                break;
            }
        }
        assertTrue("Course should be in cart", inCart);

        // 3. Simulate Checkout (Purchase)
        List<Course> selectedCourses = new ArrayList<>();
        selectedCourses.add(testCourse);
        int totalCoins = testCourse.getPrice();
        
        long orderId = orderDao.addOrder(userId, totalCoins, selectedCourses);
        assertTrue("Order ID should be valid", orderId != -1);
        
        // Remove from Cart (as done in ShoppingCartActivity)
        cartDao.removeCourseFromCart(userId, courseId);

        // 4. Verify Order Created and contains Course
        List<Order> orders = orderDao.getUserOrders(userId);
        boolean orderFound = false;
        for (Order order : orders) {
            if (order.getId() == orderId) {
                orderFound = true;
                assertNotNull("Order should have course list", order.getCourseList());
                assertFalse("Order course list should not be empty", order.getCourseList().isEmpty());
                assertEquals("Order should contain correct course", courseId, order.getCourseList().get(0).getCourseId());
                break;
            }
        }
        assertTrue("Order should be found in history", orderFound);

        // 5. Verify Removed from Cart
        cartItems = cartDao.getCartItems(userId);
        inCart = false;
        for (Course c : cartItems) {
            if (c.getCourseId() == courseId) {
                inCart = true;
                break;
            }
        }
        assertFalse("Course should be removed from cart after purchase", inCart);
    }
}
