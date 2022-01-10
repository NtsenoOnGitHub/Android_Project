package com.example.wil_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wil_project.R;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }

    public void Rate(View view) {
        Intent intent = new Intent(FeedbackActivity.this, HomeActivity.class);
        startActivity(intent);
        FeedbackActivity.this.finish();
    }
}