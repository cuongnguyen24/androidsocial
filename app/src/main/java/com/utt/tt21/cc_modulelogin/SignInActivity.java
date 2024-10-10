package com.utt.tt21.cc_modulelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;
    private EditText edtEmailIn, edtPasswordIn;
    private Button btnSignIn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        
        initui();
        initlistener();
    }

    private void initlistener() {
        layoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xác thực đăng nhập
                onClickSignIn();
            }
        });
    }

    private void onClickSignIn() {
        String email = edtEmailIn.getText().toString().trim();
        String password = edtPasswordIn.getText().toString().trim();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity(); // Đóng tất cả các activity trước đó
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(SignInActivity.this, "Đăng nhập thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initui(){

        layoutSignUp = findViewById(R.id.layout_sign_up);

        edtEmailIn = findViewById(R.id.edt_email_in);
        edtPasswordIn = findViewById(R.id.edt_password_in);
        btnSignIn = findViewById(R.id.btn_sign_in);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
    }
}