package com.example.catstudy.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.db.OrderDao;
import com.example.catstudy.model.Course;
import com.example.catstudy.model.Order;
import com.example.catstudy.util.CourseCoverUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderListActivity extends BaseActivity {
    private RecyclerView recyclerOrders;
    private OrderDao orderDao;
    private int currentUserId;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        
        // Ensure back button works
        View backBtn = findViewById(R.id.iv_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }
        
        recyclerOrders = findViewById(R.id.recycler_orders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        
        orderDao = new OrderDao(this);
        currentUserId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        
        adapter = new OrderAdapter(this, new ArrayList<>());
        recyclerOrders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != -1) {
            loadOrders();
        }
    }

    private void loadOrders() {
        List<Order> orders = orderDao.getUserOrders(currentUserId);
        adapter.setData(orders);
    }

    private static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        private final Context context;
        private List<Order> data;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        OrderAdapter(Context context, List<Order> data) {
            this.context = context;
            this.data = data;
        }

        void setData(List<Order> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = data.get(position);
            holder.tvTime.setText(dateFormat.format(new Date(order.getCreateTime())));
            holder.tvTotal.setText("实付: " + order.getTotalCoins() + "积分");
            
            holder.llCourseContainer.removeAllViews();
            if (order.getCourseList() != null) {
                for (Course course : order.getCourseList()) {
                    View courseView = LayoutInflater.from(context).inflate(R.layout.item_order_course, holder.llCourseContainer, false);
                    TextView tvTitle = courseView.findViewById(R.id.tv_course_title);
                    TextView tvPrice = courseView.findViewById(R.id.tv_course_price);
                    ImageView ivCover = courseView.findViewById(R.id.iv_course_cover);
                    
                    tvTitle.setText(course.getTitle());
                    tvPrice.setText(course.getPrice() + "积分");
                    
                    int coverResId = CourseCoverUtils.getCoverResId(context, course.getCourseId());
                    Glide.with(context)
                            .load(coverResId)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(ivCover);
                            
                    holder.llCourseContainer.addView(courseView);
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvTime;
            TextView tvTotal;
            LinearLayout llCourseContainer;

            OrderViewHolder(View itemView) {
                super(itemView);
                tvTime = itemView.findViewById(R.id.tv_order_time);
                tvTotal = itemView.findViewById(R.id.tv_order_total);
                llCourseContainer = itemView.findViewById(R.id.ll_course_container);
            }
        }
    }
}
