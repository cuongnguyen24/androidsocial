package com.utt.tt21.cc_modulelogin.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utt.tt21.cc_modulelogin.MainActivity;
import com.utt.tt21.cc_modulelogin.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000); // delay 2s
    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            // chua login
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }else {
            // da login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}