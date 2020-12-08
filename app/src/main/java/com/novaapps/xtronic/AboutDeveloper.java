package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.helpingclass.OnSwipeTouchListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutDeveloper extends AppCompatActivity {

    Button SubToYoutube , ToAdLayout , EmailMe ;
    CircleImageView DeveloperProfile ;
    LinearLayout mainLayout ;

    private InterstitialAd mInterstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-4367529646495650/5304889305";

    public static final String TAG = "AboutDeveloper" ;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.SplashScreenStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_about_developer);
        XmlHooks();
        LoadDeveloperProfile();
        SubToYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.youtube.com/channel/UCiyy3hHQ_BoD7ZhdfyncXhw?sub_confirmation=1)"));
                startActivity(i);
            }
        });
        ToAdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadAdMobInterstitialAds();

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d(TAG, "The interstitial wasn't loaded yet.");
            }
        });
        EmailMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL ,  new String[]{ "gomasu206@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Feedback For Xtronic Quiz");
                email.putExtra(Intent.EXTRA_TEXT, "//Give Your Suggestion in Simple Words (Lagna Chahiye Formal hai) ;)");
                //need this to prompts email client only
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        mainLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();

                    startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();

            }
        });

    }
    private void XmlHooks(){
        SubToYoutube = findViewById(R.id.ToDarkGomasu);
        ToAdLayout = findViewById(R.id.ToAdLayout);
        EmailMe = findViewById(R.id.EmailMe);
        DeveloperProfile = findViewById(R.id.DeveloperProfile);
        mainLayout = findViewById(R.id.aboutDeveloperLAyout);
    }
    private void LoadDeveloperProfile(){
        DatabaseReference RefToDeveloperNode = FirebaseDatabase.getInstance().getReference("DeveloperProfile");
        RefToDeveloperNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String url = snapshot.getValue(String.class);
                    Glide.with(AboutDeveloper.this).load(url).into(DeveloperProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG , "Error" + error.toString());
            }
        });
    }
    private void LoadAdMobInterstitialAds(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
}