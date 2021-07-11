package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.helpingclass.LoginDialog;
import com.novaapps.xtronic.helpingclass.OnSwipeTouchListener;
import com.novaapps.xtronic.helpingclass.UI_Data;


import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeScreen extends AppCompatActivity {

    private static final String TAG = "HomeScreen";

    //ActivityBinding
    CircleImageView LoginProfile;
    Button CreateQuizButton , JoinQuizButton , ResultQuizButton ;
    View progressViewHome ;
    View LoginDialogView ;
    ImageView XLogo ;
    ConstraintLayout childMainLayout;
    AdView AD1 ;
    //Firebase

    //Objects
    UI_Data ui_data;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.HomeScreenStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_home_screen);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("com.novaapps.xtronic"));
        XmlHookUp();
        LoadBannerAd();

        ui_data = new UI_Data(getApplicationContext());


        childMainLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                startActivity(new Intent(getApplicationContext() , AdViewLayout.class));
                overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
            }
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (ui_data.IsDeveloperViewEnabled()) {
                    startActivity(new Intent(getApplicationContext(), AboutDeveloper.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                }
            }
        });

        LoginProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG , "Opening Dialog");
                if (ui_data.IsLoggedIn())
                    CreateSignOutAlert();
                else {
                    LoginDialogLoader();
                }
            }
        });
        CreateQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ui_data.IsLoggedIn())
                    startActivity(new Intent(getApplicationContext() , QuizInfoBasic.class));
                else
                    CreateMsgAlert("You need to login  before moving Further ");
            }
        });
        JoinQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ui_data.IsLoggedIn())
                    startActivity(new Intent(getApplicationContext() , JoinQuiz.class));
                else
                    CreateMsgAlert("You need to login  before moving Further ");
            }
        });
        ResultQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ui_data.IsLoggedIn())
                  startActivity(new Intent(getApplicationContext() , Results.class));
                else
                    CreateMsgAlert("You need to login  before moving Further ");
            }
        });
        XLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ui_data.IsDeveloperViewEnabled())
                    Toast.makeText(HomeScreen.this, "Swipe Right To View The the Developer", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(HomeScreen.this, "Log In To View About The Developer", Toast.LENGTH_SHORT).show();
            }
        });

       DatabaseReference RefToAppNotification = FirebaseDatabase.getInstance().getReference("App Notification").child(String.valueOf(BuildConfig.VERSION_CODE));
       RefToAppNotification.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   CreateAppNotificationAlert(snapshot.getValue(String.class));
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (ui_data.IsLoggedIn()){
            LoadUserProfileImage();
        }

    }

    private void XmlHookUp(){
        CreateQuizButton =  findViewById(R.id.CreateQuizButton);
        JoinQuizButton =  findViewById(R.id.JoinQuizButton);
        ResultQuizButton = findViewById(R.id.ResultQuizButton);
        LoginProfile = findViewById(R.id.LoginProfile);
        progressViewHome = findViewById(R.id.progressViewHome);
        LoginDialogView = findViewById(R.id.loginDialogView);
        XLogo = findViewById(R.id.XLogo);
        childMainLayout = findViewById(R.id.constraintLayoutHomeScreen);
        AD1  = findViewById(R.id.adViewHomeScreen);
    }
    private void LoginDialogLoader(){
        LoginDialog loginDialog = new LoginDialog(getApplicationContext() , progressViewHome);
        loginDialog.show(getSupportFragmentManager() , "LoginDialog");

    }
    public void LoadUserProfileImage(){

            Log.d(TAG, "Loading : " + ui_data.get_UserProfile());
            Glide.with(this).load(ui_data.get_UserProfile()).placeholder(ResourcesCompat.getDrawable(getApplicationContext().getResources() , R.drawable.circular_view_color , getApplicationContext().getTheme())).into(LoginProfile);


    }
    private void CreateSignOutAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.Dok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                SignOut();
                LoadUserProfileImage();
            }
        });
        builder.setNegativeButton(R.string.DCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setCancelable(true);
        //Set Dialog Msg
        builder.setMessage(R.string.DMsg);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void CreateMsgAlert(String Msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setNeutralButton(R.string.Dok , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User Clicked OK
                LoginDialogLoader();
            }
        });
        // Set other dialog properties
        builder.setCancelable(true);
        //Set Dialog Msg
        builder.setMessage(Msg);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void CreateAppNotificationAlert(String Msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set other dialog properties
        builder.setCancelable(true);
        //Set Dialog Msg
        builder.setMessage(Msg);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void SignOut(){
            FirebaseAuth.getInstance().signOut();
            ui_data.LogOut();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            assert message != null;
            if (message.equals("UserLoggedIn")){
                LoadUserProfileImage();
            }
        }
    };

    private void LoadBannerAd(){
        AdRequest adRequest1 = new AdRequest.Builder().build();
        AD1.loadAd(adRequest1);
    }
}

