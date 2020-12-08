package com.novaapps.xtronic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    //Activity Binding
    private TextView AppName;

    //Objects
    UI_Data ui_data ;
    FirebaseDatabase mDatabase ;
    DatabaseReference RefToGuest;

    //Animation hookup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                                   //Hide ActionBar
        getWindow().setStatusBarColor(getResources().getColor(R.color.WhiteColor));   //Set Status Bar Color
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        XmlHookUp();

        ui_data = new UI_Data(SplashActivity.this);
        mDatabase = FirebaseDatabase.getInstance();
        RefToGuest = mDatabase.getReference("Guest");

        // Redirect
        Redirect();
        //Animate it
        ShowSplashAnimation();
    }

    private void ShowSplashAnimation(){

        Animation Dissolve_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        AppName.startAnimation(Dissolve_anim);
    }

    private void XmlHookUp(){
        AppName = findViewById(R.id.SplashAppName);
    }

    private void Redirect(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext() , HomeScreen.class);
                startActivity(intent);
                finish();
            }
        } , 5000);

    }


}