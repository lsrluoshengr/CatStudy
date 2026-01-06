package com.example.catstudy.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.catstudy.MainActivity;
import com.example.catstudy.R;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.User;

public class LoginActivity extends BaseActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if already logged in
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (sp.getBoolean("is_logged_in", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        initToolbar(""); // Hide toolbar

        // Make status bar transparent
        getWindow().getDecorView().setSystemUiVisibility(
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | 
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        userDao = new UserDao(this);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userDao.login(username, password);
            if (user != null) {
                loginSuccess(user);
            } else {
                Toast.makeText(this, "登录失败：用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup register link
        android.view.View tvRegister = findViewById(R.id.tv_register_link);
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                startActivity(new Intent(this, RegisterActivity.class));
            });
        }
    }

    private void loginSuccess(User user) {
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putInt("user_id", user.getUserId());
        editor.putString("nickname", user.getNickname());
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
