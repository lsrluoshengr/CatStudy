package com.example.catstudy.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.net.Uri;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.catstudy.R;
import com.example.catstudy.db.UserDao;
import com.example.catstudy.model.User;

import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends BaseActivity {

    private ImageView ivAvatar;
    private TextView tvUsername;
    private Spinner spGender;
    private EditText etAge;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etSignature;
    private Button btnSave;

    private UserDao userDao;
    private User currentUser;
    private String currentAvatarUrl;
    private String selectedGender;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        // Hide default action bar if present, use custom layout title
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initView();
        initData();
    }

    private void initView() {
        // initToolbar("修改资料"); // Custom layout used
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        spGender = findViewById(R.id.sp_gender);
        etAge = findViewById(R.id.et_age);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etSignature = findViewById(R.id.et_signature);
        btnSave = findViewById(R.id.btn_save);

        spGender.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        // Avatar click to change (mock implementation)
        initImagePickers();
        findViewById(R.id.ll_avatar).setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        ivAvatar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void initData() {
        userDao = new UserDao(this);
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUser = userDao.getUser(userId);
        if (currentUser == null) {
            Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate views
        currentAvatarUrl = currentUser.getAvatarUrl();
        Glide.with(this)
                .load(currentAvatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(ivAvatar);

        tvUsername.setText(currentUser.getUsername());
        setGenderSelection(currentUser.getGender());
        etAge.setText(currentUser.getAge() > 0 ? String.valueOf(currentUser.getAge()) : "");
        etPhone.setText(currentUser.getPhone());
        etEmail.setText(currentUser.getEmail());
        etSignature.setText(currentUser.getSignature());
    }

    private void setGenderSelection(String gender) {
        if (gender == null) gender = "";
        String[] options = getResources().getStringArray(R.array.gender_options);
        int index = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(gender)) { index = i; break; }
        }
        spGender.setSelection(index);
        selectedGender = options[index];
    }

    private void initImagePickers() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // Directly use the selected image
                saveImage(uri);
            }
        });
    }

    private long getFileSize(Uri uri) {
        try {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    long size = cursor.getLong(sizeIndex);
                    cursor.close();
                    return size;
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void saveImage(Uri source) {
        try {
            InputStream in = getContentResolver().openInputStream(source);
            File outFile = new File(getFilesDir(), "avatar_user_" + currentUser.getUserId() + "_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            in.close();
            fos.close();
            
            Uri finalUri = Uri.fromFile(outFile);
            currentAvatarUrl = finalUri.toString();
            Glide.with(this)
                    .load(finalUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(ivAvatar);
        } catch (Exception e) {
            Toast.makeText(this, "保存图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        if (currentUser == null) return;

        currentUser.setAvatarUrl(currentAvatarUrl);
        currentUser.setGender(selectedGender != null ? selectedGender : "");
        
        String ageStr = etAge.getText().toString().trim();
        if (!ageStr.isEmpty()) {
            try {
                currentUser.setAge(Integer.parseInt(ageStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "年龄格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        currentUser.setPhone(etPhone.getText().toString().trim());
        currentUser.setEmail(etEmail.getText().toString().trim());
        currentUser.setSignature(etSignature.getText().toString().trim());

        userDao.updateUser(currentUser);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
