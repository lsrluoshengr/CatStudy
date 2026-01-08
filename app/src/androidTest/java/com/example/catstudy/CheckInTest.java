package com.example.catstudy;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.catstudy.db.CheckInDao;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CheckInTest {
    private Context context;
    private CheckInDao checkInDao;
    private UserDao userDao;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        checkInDao = new CheckInDao(context);
        userDao = new UserDao(context);
    }

    @Test
    public void testCheckInFlow() {
        // Create unique user
        String username = "user_" + System.currentTimeMillis();
        User u = new User();
        u.setUsername(username);
        u.setPassword("pass");
        u.setNickname("Test User");
        u.setCoins(0);
        long rowId = userDao.register(u);
        assertTrue("User registration should succeed", rowId != -1);
        int uid = (int) rowId;

        // Get current coins (should be 0)
        User userBefore = userDao.getUser(uid);
        assertNotNull(userBefore);
        assertEquals(0, userBefore.getCoins());

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // 1. Check Not Checked In
        boolean isCheckedIn = checkInDao.isCheckedIn(uid, today);
        assertFalse("Should not be checked in yet", isCheckedIn);
        
        // 2. Perform Check In
        boolean success = checkInDao.checkIn(uid, today, 10);
        assertTrue("Check-in should succeed", success);
        
        // 3. Verify Coins
        User userAfter = userDao.getUser(uid);
        assertEquals("Coins should increase by 10", 10, userAfter.getCoins());
        
        // 4. Verify Consecutive Days
        int consecutive = checkInDao.getConsecutiveDays(uid);
        assertEquals("Consecutive days should be 1", 1, consecutive);
        
        // 5. Duplicate Check In
        boolean duplicate = checkInDao.checkIn(uid, today, 10);
        assertFalse("Duplicate check-in should fail", duplicate);
        
        // Verify coins didn't increase again
        User userFinal = userDao.getUser(uid);
        assertEquals("Coins should still be 10", 10, userFinal.getCoins());
    }
    
    @Test
    public void testConsecutiveDaysLogic() {
        // Create unique user
        String username = "user_cons_" + System.currentTimeMillis();
        User u = new User();
        u.setUsername(username);
        u.setPassword("pass");
        u.setNickname("Test User Cons");
        long rowId = userDao.register(u);
        int uid = (int) rowId;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        
        // Yesterday
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = sdf.format(cal.getTime());
        checkInDao.checkIn(uid, yesterday, 10);
        
        // Verify consecutive is 1
        int days = checkInDao.getConsecutiveDays(uid);
        // Note: getConsecutiveDays logic checks if LAST checkin was today or yesterday.
        // If last was yesterday, it returns yesterday's count?
        // Let's check logic:
        // if (lastDate.equals(yesterday)) { days = count; }
        // So yes, if we only checked in yesterday, consecutive days returned is 1 (stored value).
        assertEquals("Consecutive days should be 1 (yesterday)", 1, days);
        
        // Today
        String today = sdf.format(new Date());
        checkInDao.checkIn(uid, today, 10);
        
        // Verify consecutive is 2
        days = checkInDao.getConsecutiveDays(uid);
        assertEquals("Should be 2 consecutive days", 2, days);
    }

    @Test
    public void testCheckInSync() {
        // Verify Study Page and Calendar Page sync
        // Since we unified logic to use CheckInDao, we just test that CheckInDao works correctly
        // and reflects state immediately.
        
        String username = "user_sync_" + System.currentTimeMillis();
        User u = new User();
        u.setUsername(username);
        u.setPassword("pass");
        u.setNickname("Sync Test");
        long rowId = userDao.register(u);
        int uid = (int) rowId;
        
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // 1. CheckIn via Dao (simulating Study Fragment)
        boolean success = checkInDao.checkIn(uid, today, 10);
        assertTrue(success);
        
        // 2. Verify Dao isCheckedIn (simulating MyPointsActivity check)
        boolean exists = checkInDao.isCheckedIn(uid, today);
        assertTrue("Calendar page should see checkin immediately", exists);
        
        // 3. Verify Points
        User user = userDao.getUser(uid);
        assertEquals("Points should be 10", 10, user.getCoins());
    }
}
