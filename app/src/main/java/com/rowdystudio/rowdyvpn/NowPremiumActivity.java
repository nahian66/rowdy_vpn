package com.rowdystudio.rowdyvpn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class NowPremiumActivity extends AppCompatActivity {
    ImageView backToActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_premium);

        backToActivity = findViewById(R.id.finish_activity);
        backToActivity.setOnClickListener(view -> finish());
    }
}