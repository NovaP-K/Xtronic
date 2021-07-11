package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.novaapps.xtronic.helpingclass.JoinedUserInfo;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;
import com.novaapps.xtronic.helpingclass.QuizQuestions;
import com.novaapps.xtronic.helpingclass.UI_Data;


import java.util.ArrayList;
import java.util.Objects;

public class GetIdentifier extends AppCompatActivity {

    //XmlHooks
    private Button StartQuizButton;
    private TextView EIIdentifier ;
    private EditText IdentifierBox ;
    private View progressView ;

    //Variable
    private String Identifier ;
    private String ID ;
    private Long QSize ;
    private static final String TAG = "GetIdentifier" ;

    //Objects
    QuizBasicInfoData quizBasicInfoData ;
    JoinedUserInfo joinedUserInfo ;
    private UI_Data uiData ;
    CustomProgress customProgress;
    ArrayList<QuizQuestions> quizQuestionsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.GetIdentifierStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_get_identifier);
        XmlHookUp();
        ObjectHooks();


        SetAskIdentifier();

        StartQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getIdentifier();
                SetJoinedUserClass();
                UploadOnDatabase();

            }
        });



    }

    private void XmlHookUp(){
        StartQuizButton = findViewById(R.id.startQuizButton);
        EIIdentifier = findViewById(R.id.EIdentifier);
        IdentifierBox = findViewById(R.id.IdentifierBox);
        progressView = findViewById(R.id.progressViewGetIdentifier);
    }

    private void ObjectHooks(){
        ID = getIntent().getStringExtra("ID");
        QSize = getIntent().getLongExtra("Size" , 0);
        quizBasicInfoData = (QuizBasicInfoData) getIntent().getSerializableExtra("QuizInfoBasic");
        joinedUserInfo = new JoinedUserInfo();
        uiData = new UI_Data(getApplicationContext());
        customProgress = new CustomProgress(getApplicationContext() , progressView);
        quizQuestionsArrayList = new ArrayList<>();
    }

    private void SetAskIdentifier(){
        String Ask = "Enter " + quizBasicInfoData.get_IdentifierType();
        EIIdentifier.setText(Ask);
    }

    private void getIdentifier(){
        Identifier = IdentifierBox.getText().toString() ;
    }

    private void SetJoinedUserClass(){
        joinedUserInfo.set_Email(uiData.get_UserEmail());
        joinedUserInfo.set_Name(uiData.get_UserName());
        joinedUserInfo.set_Identifier(Identifier);
    }

    private void UploadOnDatabase(){
        DatabaseReference RefToQuizAnswer = FirebaseDatabase.getInstance().getReference("QuizAnswer").child(ID);
        customProgress.startProgressBar();

        RefToQuizAnswer.child(uiData.get_UserUid()).setValue(joinedUserInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ToUserCount();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customProgress.stopProgressBar();
                Toast.makeText(GetIdentifier.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ToUserCount(){
        final DatabaseReference RefToUsersJoinedCount = FirebaseDatabase.getInstance().getReference("User").child(uiData.get_UserUid()).child("QuizJoined");

        RefToUsersJoinedCount.child("Count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long count = snapshot.getValue(Long.class);
                UpdateLastCount(count , RefToUsersJoinedCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void UpdateLastCount(Long count , DatabaseReference RefToUsersJoinedCount) {
        count++;
        RefToUsersJoinedCount.child("Count").setValue(count);
        RefToUsersJoinedCount.child(String.valueOf(count)).setValue(ID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                GetAllQuestions();
            }
        });

    }

    private void UpdateEnrolledUser(){
        final DatabaseReference RefToEnrolledUser = FirebaseDatabase.getInstance().getReference("QuizInfo").child(ID).child("Enrolled Users");

        RefToEnrolledUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long Count = snapshot.getChildrenCount();
                ++Count;
                RefToEnrolledUser.child(String.valueOf(Count)).setValue(uiData.get_UserUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                   ToNextActivity();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void GetAllQuestions(){
        DatabaseReference RefToQuestionBankID= FirebaseDatabase.getInstance().getReference("QuestionBank").child(quizBasicInfoData.get_CreatorUid()).child(ID);

        RefToQuestionBankID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (int i = 0; i < QSize; i++) {
                        new QuizQuestions();
                        QuizQuestions quizQuestions;
                        quizQuestions = snapshot.child(String.valueOf(i)).getValue(QuizQuestions.class);
                        quizQuestionsArrayList.add(quizQuestions);
                    }
                    customProgress.stopProgressBar();
                    if (QSize == quizQuestionsArrayList.size())
                        UpdateEnrolledUser();
                }else
                    CreateMsgAlert();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GetIdentifier.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                customProgress.stopProgressBar();
              }
          });

       }
    private void CreateMsgAlert(){
        customProgress.stopProgressBar();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set other dialog properties
        builder.setCancelable(true);
        //Set Dialog Msg
        builder.setMessage("Internal Error : Question List Empty \n Contact Quiz creator");
        builder.setTitle("Error");
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void ToNextActivity(){
        Intent intent = new Intent(getApplicationContext() , QuizTime.class);
        intent.putExtra("QuizInfoBasic" , quizBasicInfoData);
        intent.putExtra("ID" , ID);
        Bundle bundle = new Bundle();
        bundle.putSerializable("QuizQuestionsBundle", quizQuestionsArrayList);
        intent.putExtra("QuestionArray",bundle);
        startActivity(intent);
        finish();
    }

}