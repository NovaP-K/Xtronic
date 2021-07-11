package com.novaapps.xtronic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Objects;


public class AdViewLayout extends AppCompatActivity {


    public static final String TAG = "AdViewLayout.java";



    //AdMob layout
    private AdView Ad1;
    private AdView Ad2;
    AdLayout Ad3 ;
    AdLayout Ad4 ;
    TextView AdLoadedStatus ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.GetIdentifierStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_ad_view_layout);
        XmlHooks();

        LoadBannerAds();
        LoadAmazonAds();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


    }

    private void HideAdLoadedStatus(){
        AdLoadedStatus.setVisibility(View.GONE);
    }
    private void XmlHooks(){
        Ad3 = findViewById(R.id.AmazonAdView1);
        Ad4 = findViewById(R.id.AmazonAdView2);
        Ad1 = findViewById(R.id.AdMobAd1);
        Ad2 = findViewById(R.id.AdMobAd2);
        AdLoadedStatus = findViewById(R.id.adLoadedStatus);
    }
    private void LoadBannerAds(){
        AdRequest adRequest1 = new AdRequest.Builder().build();
        Ad1.loadAd(adRequest1);

        AdRequest adRequest2 = new AdRequest.Builder().build();
        Ad2.loadAd(adRequest2);

        Ad1.setAdListener(new AdListener(){

            public void onAdLoaded() {
                HideAdLoadedStatus();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
            }
        });
        Ad2.setAdListener(new AdListener(){
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                HideAdLoadedStatus();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
            }
        });

    }
    private void LoadAmazonAds(){
        AdRegistration.enableLogging(false);
        AdRegistration.enableTesting(false);

        try {
            AdRegistration.setAppKey("2d5f4ea791db43d3831a21545c34353d");
        } catch (final IllegalArgumentException e){
            Log.e("Log_TAG" , "IllegalArgumentException thrown : " + e.toString());
        }

        Ad3.loadAd();

        Ad4.loadAd();

        Ad3.setListener(new com.amazon.device.ads.AdListener() {
            @Override
            public void onAdLoaded(Ad ad, AdProperties adProperties) {
                Ad3.showAd();
                HideAdLoadedStatus();
            }

            @Override
            public void onAdFailedToLoad(Ad ad, AdError adError) {
            }

            @Override
            public void onAdExpanded(Ad ad) {

            }

            @Override
            public void onAdCollapsed(Ad ad) {

            }

            @Override
            public void onAdDismissed(Ad ad) {

            }
        });

        Ad4.setListener(new com.amazon.device.ads.AdListener() {
            @Override
            public void onAdLoaded(Ad ad, AdProperties adProperties) {
                Ad3.showAd();
                HideAdLoadedStatus();
            }

            @Override
            public void onAdFailedToLoad(Ad ad, AdError adError) {
            }

            @Override
            public void onAdExpanded(Ad ad) {

            }

            @Override
            public void onAdCollapsed(Ad ad) {

            }

            @Override
            public void onAdDismissed(Ad ad) {

            }
        });

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Ad1.destroy();
        Ad2.destroy();
        Ad3.destroy();
        Ad4.destroy();
    }
}