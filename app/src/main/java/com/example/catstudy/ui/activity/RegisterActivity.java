package com.example.catstudy.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.catstudy.MainActivity;
import com.example.catstudy.R;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.User;
import java.util.Random;

public class RegisterActivity extends BaseActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etCaptcha;
    private Button btnGetCaptcha;
    private Button btnRegister;
    private CheckBox cbAgreement;
    private TextView tvLoginLink;
    private UserDao userDao;
    private String generatedCaptcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolbar("注册账号");

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etCaptcha = findViewById(R.id.et_captcha);
        btnGetCaptcha = findViewById(R.id.btn_get_captcha);
        btnRegister = findViewById(R.id.btn_register);
        cbAgreement = findViewById(R.id.cb_agreement);
        tvLoginLink = findViewById(R.id.tv_login_link);
        
        userDao = new UserDao(this);

        btnGetCaptcha.setOnClickListener(v -> generateCaptcha());
        
        btnRegister.setOnClickListener(v -> handleRegister());

        tvLoginLink.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }

    private void generateCaptcha() {
        Random random = new Random();
        int captcha = 1000 + random.nextInt(9000);
        generatedCaptcha = String.valueOf(captcha);
        Toast.makeText(this, "验证码: " + generatedCaptcha, Toast.LENGTH_LONG).show();
        etCaptcha.setText(generatedCaptcha); // Auto fill for convenience
    }

    private void handleRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String captcha = etCaptcha.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
             Toast.makeText(this, "密码强度不足：需至少6位且包含字母和数字", Toast.LENGTH_SHORT).show();
             return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(captcha) || !captcha.equals(generatedCaptcha)) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbAgreement.isChecked()) {
            Toast.makeText(this, "请阅读并同意注册协议", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDao.isUsernameExists(username)) {
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setNickname("User_" + username);
        newUser.setCoins(0);
        
        long id = userDao.register(newUser);
        if (id > 0) {
            newUser.setUserId((int) id);
            loginSuccess(newUser);
        } else {
            Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginSuccess(User user) {
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putInt("user_id", user.getUserId());
        editor.putString("nickname", user.getNickname());
        editor.apply();

        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
