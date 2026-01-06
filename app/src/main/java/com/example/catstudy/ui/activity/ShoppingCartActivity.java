package com.example.catstudy.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.db.CartDao;
import com.example.catstudy.db.OrderDao;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.Course;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CheckBox cbSelectAll;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartDao cartDao;
    private UserDao userDao;
    private OrderDao orderDao;
    private int currentUserId;
    private CartAdapter adapter;
    private List<Course> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        initToolbar("购物车");
        recyclerView = findViewById(R.id.recycler_cart);
        cbSelectAll = findViewById(R.id.cb_select_all);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartDao = new CartDao(this);
        userDao = new UserDao(this);
        orderDao = new OrderDao(this);
        currentUserId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        cartItems = cartDao.getCartItems(currentUserId);
        adapter = new CartAdapter(this, cartItems, total -> tvTotal.setText("合计：" + total + "积分"));
        recyclerView.setAdapter(adapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.setAllSelected(isChecked);
            updateTotal();
        });
        btnCheckout.setOnClickListener(v -> checkout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartItems = cartDao.getCartItems(currentUserId);
        adapter.setData(cartItems);
        updateTotal();
    }

    private void updateTotal() {
        int total = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            if (adapter.isSelected(i)) {
                total += Math.max(0, cartItems.get(i).getPrice());
            }
        }
        tvTotal.setText("合计：" + total + "积分");
    }

    private void checkout() {
        int total = 0;
        int selectedCount = 0;
        List<Course> selectedCourses = new ArrayList<>();
        for (int i = 0; i < cartItems.size(); i++) {
            if (adapter.isSelected(i)) {
                total += Math.max(0, cartItems.get(i).getPrice());
                selectedCourses.add(cartItems.get(i));
                selectedCount++;
            }
        }
        if (selectedCount == 0) {
            Toast.makeText(this, "请选择要结算的课程", Toast.LENGTH_SHORT).show();
            return;
        }
        int coins = userDao.getUser(currentUserId).getCoins();
        if (coins < total) {
            Toast.makeText(this, "积分不足，当前余额：" + coins, Toast.LENGTH_SHORT).show();
            return;
        }
        userDao.updateCoins(currentUserId, coins - total);
        orderDao.addOrder(currentUserId, total, selectedCourses);
        
        for (Course c : selectedCourses) {
            cartDao.removeCourseFromCart(currentUserId, c.getCourseId());
        }
        
        Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    private static class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
        private final Context context;
        private final LayoutInflater inflater;
        private List<Course> data;
        private final List<Boolean> selected;
        private final OnTotalChangeListener totalListener;

        CartAdapter(Context context, List<Course> data, OnTotalChangeListener listener) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.data = new ArrayList<>(data);
            this.selected = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) selected.add(false);
            this.totalListener = listener;
        }

        void setData(List<Course> newData) {
            this.data = new ArrayList<>(newData);
            this.selected.clear();
            for (int i = 0; i < newData.size(); i++) selected.add(false);
            notifyDataSetChanged();
            notifyTotalChanged();
        }

        void setAllSelected(boolean all) {
            for (int i = 0; i < selected.size(); i++) {
                selected.set(i, all);
            }
            notifyDataSetChanged();
            notifyTotalChanged();
        }

        boolean isSelected(int position) {
            if (position < 0 || position >= selected.size()) return false;
            return selected.get(position);
        }

        @Override
        public CartViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_cart_course, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CartViewHolder holder, int position) {
            Course c = data.get(position);
            holder.tvTitle.setText(c.getTitle());
            holder.tvPrice.setText(c.getPrice() + "积分");
            Glide.with(context)
                    .load(c.getCoverUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.ivCover);
            holder.cbItem.setOnCheckedChangeListener(null);
            holder.cbItem.setChecked(selected.get(position));
            holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                selected.set(position, isChecked);
                notifyTotalChanged();
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private void notifyTotalChanged() {
            int total = 0;
            for (int i = 0; i < data.size(); i++) {
                if (selected.get(i)) {
                    total += Math.max(0, data.get(i).getPrice());
                }
            }
            if (totalListener != null) totalListener.onTotalChanged(total);
        }

        static class CartViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbItem;
            TextView tvTitle;
            TextView tvPrice;
            android.widget.ImageView ivCover;
            CartViewHolder(View itemView) {
                super(itemView);
                cbItem = itemView.findViewById(R.id.cb_item);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvPrice = itemView.findViewById(R.id.tv_price);
                ivCover = itemView.findViewById(R.id.iv_cover);
            }
        }
    }

    interface OnTotalChangeListener {
        void onTotalChanged(int total);
    }
}
