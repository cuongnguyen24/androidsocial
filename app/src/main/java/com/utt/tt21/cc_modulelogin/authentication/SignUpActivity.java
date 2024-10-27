package com.utt.tt21.cc_modulelogin.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utt.tt21.cc_modulelogin.MainActivity;
import com.utt.tt21.cc_modulelogin.R;

public class SignUpActivity extends AppCompatActivity {
    private LinearLayout layoutSignIn;
    private EditText edtEmail, edtPassword, edtConfirmPassword, edtFullName;
    private Button btnSignUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUi();
        initListener();
    }

    private void initListener() {
        layoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
    }

    private void onClickSignUp() {
        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        String strConfirmPassword = edtConfirmPassword.getText().toString().trim();
        String strFullName = edtFullName.getText().toString().trim();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (strEmail.isEmpty() || strPassword.isEmpty()|| strFullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra email có kết thúc bằng @gmail.com hay không
        if (!strEmail.endsWith("@gmail.com")) {
            Toast.makeText(this, "Tài khoản phải sử dụng email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem mật khẩu và xác nhận mật khẩu có khớp nhau không
        if (!strPassword.equals(strConfirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        auth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // tạo than công thì lấy Uid của user mới tạo
                            FirebaseUser user = auth.getCurrentUser();
                            String userId = user.getUid();

                            // Lưu fullName vào Realtime Database
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            databaseRef.child("nameProfile").setValue(strFullName);
                            databaseRef.child("desProfile").setValue("");
                            databaseRef.child("emailProfile").setValue(strEmail);
                            databaseRef.child("imgProfile").setValue(1);
                            databaseRef.child("followers").setValue(0); // Số lượng người theo dõi ban đầu là 0
                            databaseRef.child("followings").setValue(0); // Số lượng người mà người dùng đang theo dõi là 0

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity(); // đóng tất cá các activity trước MainActivity
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initUi(){
        layoutSignIn = findViewById(R.id.layout_sign_in);
        edtFullName = findViewById(R.id.edt_full_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký...");
    }
}