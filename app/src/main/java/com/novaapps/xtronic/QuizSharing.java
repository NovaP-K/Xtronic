package com.novaapps.xtronic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Objects;

public class QuizSharing extends AppCompatActivity {

    //XmlHooks
    private TextView ShareIdView ;
    private Button ShareToWhatsApp , ShareToFb , ShareToMsg , ShareToOther , CopyShareId;


    //Variable
    private String ID ;
    String TextToShare ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.QuizSharingStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_quiz_sharing);
        XmlHookUp();

        ID = getIntent().getStringExtra("ID");

        SetShareID();
        SetTextToShare();

        ShareToWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhatsAppShare();
            }
        });

        ShareToFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FbAppShare();
            }
        });

        ShareToMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MsgAppShare();
            }
        });

        ShareToOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OtherAppShare();
            }
        });

        CopyShareId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyQuizID();
            }
        });

    }

    private void XmlHookUp(){
        ShareIdView = findViewById(R.id.ShareID);
        ShareToWhatsApp = findViewById(R.id.ShareToWhatsApp);
        ShareToFb = findViewById(R.id.ShareToFaceBook);
        ShareToMsg = findViewById(R.id.ShareToMessenger);
        ShareToOther = findViewById(R.id.ShareToOther);
        CopyShareId = findViewById(R.id.CopyShareID);
    }

    private void SetShareID(){
        String HashID = "#" + ID;
        ShareIdView.setText(HashID);
    }

    private void SetTextToShare(){
        TextToShare = "Join The Xtronic Quiz : #" + ID +
                " \n You can Join The Quiz by  Downloading Xtronic App and then Clicking the Link below \n " +
                "https://xtronicquiz.com/joinquiz/id/"+ ID;
    }

    private void WhatsAppShare(){
        Intent WhatsAppIntent = new Intent(Intent.ACTION_SEND);
        WhatsAppIntent.setType("text/plain");
        WhatsAppIntent.setPackage("com.whatsapp");
        WhatsAppIntent.putExtra(Intent.EXTRA_TEXT, TextToShare);
        try {
          startActivity(WhatsAppIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void FbAppShare(){
        Intent FbAppIntent = new Intent(Intent.ACTION_SEND);
        FbAppIntent.setType("text/plain");
        FbAppIntent.setPackage("com.facebook.katana");
        FbAppIntent.putExtra(Intent.EXTRA_TEXT, TextToShare);
        try {
            startActivity(FbAppIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Facebook not found on Device", Toast.LENGTH_SHORT).show();
        }
    }

    private void MsgAppShare(){
        Intent MsgAppIntent = new Intent(Intent.ACTION_SEND);
        MsgAppIntent.setType("text/plain");
        MsgAppIntent.setPackage("com.facebook.orca");
        MsgAppIntent.putExtra(Intent.EXTRA_TEXT, TextToShare);
        try {
            startActivity(MsgAppIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Messenger Not Found not found on Device", Toast.LENGTH_SHORT).show();
        }
    }

    private void OtherAppShare(){
        Intent OtherAppIntent = new Intent(Intent.ACTION_SEND);
        OtherAppIntent.setType("text/plain");
        OtherAppIntent.putExtra(Intent.EXTRA_TEXT, TextToShare);
        try {
            startActivity(OtherAppIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No App Installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void CopyQuizID(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("QuizID", ID);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "ID Copied", Toast.LENGTH_SHORT).show();
    }

}