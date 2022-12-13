package com.example.labb3ctimesl.activites;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.services.AlarmService;

import java.util.Objects;

public class RingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Button button = findViewById(R.id.dismiss);
        button.setOnClickListener(v -> {
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("dismissed", true);
            startActivity(intent);
        });

    }
}