package com.utt.tt21.cc_modulelogin.profile.accountmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.utt.tt21.cc_modulelogin.R;

public class ImageDisplayActivity extends AppCompatActivity {

    private ImageView imageView; // Khai báo ImageView để hiển thị ảnh
    private ImageButton btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this); // Đảm bảo tính năng edge-to-edge được bật
        setContentView(R.layout.activity_image_display); // Đặt layout XML trước khi truy cập vào ImageView

        // Khởi tạo ImageView
        imageView = findViewById(R.id.imageView);

        // Nhận URL ảnh từ Intent
        String imageUrl = getIntent().getStringExtra("imageUrl");
        if (imageUrl != null) {
            // Sử dụng Glide để tải ảnh từ URL
            Glide.with(this)
                    .load(imageUrl)
                    .error(R.drawable.ic_default_user) // Ảnh mặc định nếu có lỗi
                    .into(imageView);
        } else {
            // Thông báo nếu imageUrl không có dữ liệu
            Toast.makeText(this, "Không có URL ảnh để hiển thị", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có ảnh để hiển thị
        }

        // Khởi tạo Btn close
        btnClose = findViewById(R.id.btn_close_guest);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
