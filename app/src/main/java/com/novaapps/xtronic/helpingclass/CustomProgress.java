package com.novaapps.xtronic.helpingclass;

import android.content.Context;

import android.view.View;

import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.novaapps.xtronic.R;

public class CustomProgress {

    Context context ;
    ConstraintLayout progressBarBg , progressBarCircle;
    ProgressBar progressBar;

    public CustomProgress(Context ct , View view) {
        context = ct;
        progressBarBg = view.findViewById(R.id.progressBarBackground);
        progressBarCircle = view.findViewById(R.id.progressBarCircle);
        progressBar = view.findViewById(R.id.progressBar);
    }

    public void startProgressBar(){
        progressBarBg.setVisibility(View.VISIBLE);
        progressBarCircle.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar(){
        progressBarBg.setVisibility(View.GONE);
        progressBarCircle.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

}
