package com.novaapps.xtronic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.Objects;

public class QuizSubmission extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.QuizSubmissionStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_quiz_submission);

        TextView RealScore = findViewById(R.id.RealScore);
        Button ViewSomeAds = findViewById(R.id.ViewSomeAdsQuizSubmission);


        int Score = getIntent().getIntExtra("Score" , 0);
        int Total = getIntent().getIntExtra("Total" , 0);

        String FormattedScore = Score + "/" + Total ;

        RealScore.setText(FormattedScore);
        ViewSomeAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext() , AdViewLayout.class));
            }
        });

    }
}