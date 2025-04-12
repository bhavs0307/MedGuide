package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.MainActivity;
import com.example.medicinereminder.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);  // Set the splash screen layout

        // Optional: Add a delay to show the splash screen for a few seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity after the delay
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Finish SplashActivity to prevent it from appearing again
            }
        }, 3000);  // 3-second delay (adjust the time as needed)
    }
}