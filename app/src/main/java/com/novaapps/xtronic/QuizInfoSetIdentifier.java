package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.helpingclass.CustomProgress;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class QuizInfoSetIdentifier extends AppCompatActivity {

    //ActivityBinding
    EditText Identifier;
    Button ProceedButton;
    View progressView ;

    //Variables
    String ID;
    public static final String TAG = "QuizInfoSetIdentifier";

    //Objects
    UI_Data uiData ;
    QuizBasicInfoData QData ;
    CustomProgress customProgress;

    //Firebase
    FirebaseDatabase mDatabase;
    DatabaseReference RefToInfo , RefToCreateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.QuizInfoSetIdentifierStatus)); //Set Status Bar Color
        setContentView(R.layout.activity_quiz_info_set_identifier);
        XmlHookUp();
        ObjectsHooks();


        CreateMsgAlert();

        ProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QData.set_IdentifierType(Identifier.getText().toString());
                QData.set_DateCreated(getCurrentDateAndTime());
                UploadToDatabase();
            }
        });

    }

    private void XmlHookUp(){
        Identifier = findViewById(R.id.IdentifierInput);
        ProceedButton = findViewById(R.id.ProceedButton);
        progressView = findViewById(R.id.progressViewSetIdentifier);
    }

    private void ObjectsHooks(){
        uiData = new UI_Data(getApplicationContext());

        mDatabase = FirebaseDatabase.getInstance();
        RefToInfo = mDatabase.getReference("QuizInfo");
        RefToCreateCount = mDatabase.getReference("User").child(uiData.get_UserUid()).child("QuizCreated");

        customProgress = new CustomProgress(getApplicationContext() , progressView);
        ID = getIntent().getStringExtra("ID");
        QData = (QuizBasicInfoData) getIntent().getSerializableExtra("QuizBasicInfo");
    }

    private void UploadToDatabase(){
        customProgress.startProgressBar();
        RefToInfo.child(ID).setValue(QData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ToUserCount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customProgress.stopProgressBar();
                Log.d(TAG , e.toString());
                Toast.makeText(getApplicationContext(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ToUserCount(){
        RefToCreateCount.child("Count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getValue(Long.class);
                AddIDToLastCount(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgress.stopProgressBar();
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(), "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void AddIDToLastCount(Long count){

        count++;
        RefToCreateCount.child("Count").setValue(count);
        RefToCreateCount.child(String.valueOf(count)).setValue(ID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               ToNextActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customProgress.stopProgressBar();
                Log.d(TAG , e.toString());
                Toast.makeText(getApplicationContext(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void CreateMsgAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setNeutralButton("Understood" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User Clicked OK
                dialogInterface.dismiss();
            }
        });
        // Set other dialog properties
        builder.setCancelable(false);
        //Set Dialog Msg
        builder.setMessage(R.string.SIdentifierHelp);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String getCurrentDateAndTime(){

        Date date = new Date();

        return DateFormat.getDateTimeInstance().format(date);
    }

    private void ToNextActivity(){
        customProgress.stopProgressBar();
        Intent intent = new Intent(getApplicationContext() , SetQuiz.class);
        intent.putExtra("ID" , ID);
        startActivity(intent);
        finish();
    }


}