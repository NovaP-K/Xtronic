package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.helpingclass.CustomProgress;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;
import com.novaapps.xtronic.helpingclass.UI_Data;


import java.util.Objects;


public class QuizInfoBasic extends AppCompatActivity {

    //Widgets
    EditText CreatorName , TopicName , Duration ;
    Button NextButton;
    View progressView ;
    CheckBox TimerTypeButton ;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference RefToBasic;
    DatabaseReference RefToCreateCount;

    //Variable
    String ID;
    private static final String TAG = "QuizInfoBasic" ;
    Boolean IsChecked = false ;

    //Objects
    QuizBasicInfoData QData;
    UI_Data ui_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.QuizInfoBasicStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_quiz_info_basic);
        XmlHookUp();
        ObjHooks();

        QData = new QuizBasicInfoData() ;
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNull()) {
                    long __Duration__ ;
                    if (Duration.getText().toString().isEmpty())
                        __Duration__ = 0 ;
                    else
                       __Duration__ =  Long.parseLong(Duration.getText().toString());

                    QData.InsertData(CreatorName.getText().toString(), TopicName.getText().toString(), __Duration__, IsChecked);
                    QData.set_CreatorUid(ui_data.get_UserUid());
                    ID = CheckID();
                }
                else {
                    CreateNullAlert();
                }

            }
        });

        TimerTypeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                IsChecked = b;
            }
        });

    }
    private void XmlHookUp(){
        CreatorName = findViewById(R.id.CreatorName);
        TopicName = findViewById(R.id.TopicName);
        Duration = findViewById(R.id.Duration);
        NextButton = findViewById(R.id.NextButton);
        progressView = findViewById(R.id.progressViewInfoBasic);
        TimerTypeButton = findViewById(R.id.timerType);
    }
    private void ObjHooks(){

        ui_data = new UI_Data(QuizInfoBasic.this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        RefToBasic = mDatabase.getReference("QuizInfo");
        RefToCreateCount = mDatabase.getReference("User").child(ui_data.get_UserUid()).child("QuizCreated");

    }
    private boolean CheckNull(){
        return !CreatorName.getText().toString().isEmpty() && !TopicName.getText().toString().isEmpty();
    }
    private String RandomIDGenerator(){

        int IDLength = 5 ;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder TempID = new StringBuilder(IDLength);

        for (int i=0 ; i < IDLength ; i++){
            int index  = (int) (AlphaNumericString.length() * Math.random());
            TempID.append(AlphaNumericString.charAt(index));

        }

        return TempID.toString();

    }
    private String CheckID(){
        final CustomProgress customProgress = new CustomProgress(getApplicationContext() , progressView);
        customProgress.startProgressBar();
        final String temp = RandomIDGenerator();

        RefToBasic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(temp)){
                    Log.d(TAG , "ID Found " + temp);
                    RefToBasic.child(temp).child("Creator Uid").setValue(ui_data.get_UserUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            customProgress.stopProgressBar();
                            ToNextActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            customProgress.stopProgressBar();
                            Log.d(TAG , e.toString());
                            Toast.makeText(QuizInfoBasic.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    CheckID();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgress.stopProgressBar();
                Log.d(TAG , error.toString());
                Toast.makeText(QuizInfoBasic.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return temp;
    }
    private void CreateNullAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set other dialog properties
        builder.setCancelable(true);
        //Set Dialog Msg
        builder.setMessage("You Need to Enter Required Details to Proceed");
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    private void ToNextActivity(){
        Intent intent = new Intent(getApplicationContext() , QuizInfoSetIdentifier.class);
        intent.putExtra("ID" , ID);
        intent.putExtra("QuizBasicInfo" , QData);
        startActivity(intent);
        finish();
    }

}