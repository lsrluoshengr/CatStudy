package com.example.catstudy.ui.activity;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catstudy.R;
import com.example.catstudy.db.DBHelper;
import com.example.catstudy.db.UserDao;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import android.view.LayoutInflater;
import android.view.View;

public class MyPointsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_points);
        initToolbar("我的积分");
        TextView tvTotal = findViewById(R.id.tv_total_points);
        Button btnCheckIn = findViewById(R.id.btn_check_in);
        RecyclerView recycler = findViewById(R.id.recycler_calendar);
        recycler.setLayoutManager(new GridLayoutManager(this, 7));

        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UserDao userDao = new UserDao(this);
        tvTotal.setText(String.valueOf(userDao.getUser(userId).getCoins()));

        Set<Integer> checkedDays = getCheckedDaysOfCurrentMonth(userId);
        CalendarAdapter adapter = new CalendarAdapter(checkedDays);
        recycler.setAdapter(adapter);

        btnCheckIn.setOnClickListener(v -> {
            if (isCheckedInToday(userId)) {
                Toast.makeText(this, "今天已签到", Toast.LENGTH_SHORT).show();
                return;
            }
            addCheckIn(userId, 5);
            int coins = userDao.getUser(userId).getCoins();
            userDao.updateCoins(userId, coins + 5);
            tvTotal.setText(String.valueOf(coins + 5));
            Set<Integer> updated = getCheckedDaysOfCurrentMonth(userId);
            adapter.setCheckedDays(updated);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "签到成功，获得5积分", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isCheckedInToday(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, null, "user_id=? AND checkin_date=?",
                new String[]{String.valueOf(userId), today}, null, null, null);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    private void addCheckIn(int userId, int points) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        db.execSQL("INSERT INTO " + DBHelper.TABLE_CHECKIN + " (user_id, checkin_date, points) VALUES (?, ?, ?)",
                new Object[]{userId, today, points});
        db.close();
    }

    private Set<Integer> getCheckedDaysOfCurrentMonth(int userId) {
        Calendar cal = Calendar.getInstance();
        String prefix = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.getTime());
        Set<Integer> days = new HashSet<>();
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CHECKIN, new String[]{"checkin_date"}, "user_id=? AND checkin_date LIKE ?",
                new String[]{String.valueOf(userId), prefix + "%"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("checkin_date"));
                String[] parts = date.split("-");
                if (parts.length == 3) {
                    try {
                        int day = Integer.parseInt(parts[2]);
                        days.add(day);
                    } catch (Exception ignored) {}
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return days;
    }

    private static class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
        private final List<Integer> days;
        private final int firstDayOffset;
        private final int maxDay;
        private Set<Integer> checkedDays;

        CalendarAdapter(Set<Integer> checkedDays) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            firstDayOffset = cal.get(Calendar.DAY_OF_WEEK) - 1;
            maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            days = new ArrayList<>();
            for (int i = 1; i <= maxDay; i++) days.add(i);
            this.checkedDays = new HashSet<>(checkedDays);
        }

        void setCheckedDays(Set<Integer> set) {
            this.checkedDays = new HashSet<>(set);
        }

        @Override
        public CalendarViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
            return new CalendarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CalendarViewHolder holder, int position) {
            int index = position - firstDayOffset;
            if (index < 0 || index >= days.size()) {
                holder.bindEmpty();
            } else {
                int day = days.get(index);
                holder.bindDay(day, checkedDays.contains(day));
            }
        }

        @Override
        public int getItemCount() {
            return firstDayOffset + maxDay + ((7 - (firstDayOffset + maxDay) % 7) % 7);
        }
    }

    private static class CalendarViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDay;
        private final View container;

        CalendarViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            container = itemView.findViewById(R.id.container_day);
        }

        void bindEmpty() {
            tvDay.setText("");
            container.setBackgroundColor(0x00000000);
        }

        void bindDay(int day, boolean checked) {
            tvDay.setText(String.valueOf(day));
            if (checked) {
                container.setBackgroundColor(itemView.getResources().getColor(R.color.classical_red));
                tvDay.setTextColor(itemView.getResources().getColor(R.color.classical_ivory));
            } else {
                container.setBackgroundColor(itemView.getResources().getColor(android.R.color.transparent));
                tvDay.setTextColor(itemView.getResources().getColor(R.color.classical_text_primary));
            }
        }
    }
}
