package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.helpingclass.CustomProgress;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.util.List;
import java.util.Objects;

public class JoinQuiz extends AppCompatActivity {

    //XmlHooks
    private Button JoinQuiz;
    private EditText QuizCode;
    private View progressView ;

    //Variable
    private static final String TAG = "JoinQuiz.class";
    private String ID;
    private Long Size ;

    //Objects
    QuizBasicInfoData quizBasicInfoData;
    CustomProgress customProgress ;
    UI_Data ui_data ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.JoinQuizStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_join_quiz);
        XmlHookUp();

        ui_data = new UI_Data(getApplicationContext());
        quizBasicInfoData = new QuizBasicInfoData();
        customProgress = new CustomProgress(getApplicationContext() , progressView);

        Intent intent=getIntent();

        if(intent!=null&&intent.getData()!=null){
            if (ui_data.IsLoggedIn()){
                Uri uri = intent.getData();
                String ID_Intent ;
                List<String> pathSegments = uri.getPathSegments();
                if(pathSegments.size()>0) {
                    ID_Intent = pathSegments.get(2);  // This will give you prefix as path
                    QuizCode.setText(ID_Intent.toUpperCase());
                }
            }
        }

        JoinQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgress.startProgressBar();
                getQuizCode();
                CheckQuizAvailability();

            }
        });

    }

    private void XmlHookUp() {
        JoinQuiz = findViewById(R.id.joinButton);
        QuizCode = findViewById(R.id.JoinCodeText);
        progressView = findViewById(R.id.progressViewJoinQuiz);
    }

    private void getQuizCode() {
        ID = QuizCode.getText().toString();
    }

    private void CheckQuizAvailability() {
        final DatabaseReference RefToQuizInfo = FirebaseDatabase.getInstance().getReference("QuizInfo").child(ID);

        RefToQuizInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG , "ID Available");
                    CheckIfUserHasGivenTheQuiz(RefToQuizInfo);
                } else {
                    Log.d(TAG , "ID Not Available");
                    Toast.makeText(JoinQuiz.this, "No Found", Toast.LENGTH_SHORT).show();
                    customProgress.stopProgressBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgress.stopProgressBar();
                Log.d(TAG , "Error Encountered");
                Toast.makeText(JoinQuiz.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void CheckIfUserHasGivenTheQuiz(final DatabaseReference RefToQuizInfo){

        final DatabaseReference RefToEnrolledUser = FirebaseDatabase.getInstance().getReference("QuizInfo").child(ID).child("Enrolled Users");

        RefToEnrolledUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long Count = snapshot.getChildrenCount();
                    boolean IsTaken = true;
                    if (Count == 0){
                        getQuizInfo(RefToQuizInfo);
                    }
                    else{
                        for (int i=1 ; i <=Count ; i++){
                            String TempUid = snapshot.child(String.valueOf(i)).getValue(String.class);
                            assert TempUid != null;
                            if (TempUid.equals(ui_data.get_UserUid())){
                                customProgress.stopProgressBar();
                                Toast.makeText(JoinQuiz.this, "It appears , You have Already attempted the Quiz", Toast.LENGTH_LONG).show();
                                IsTaken = false ;
                                break;
                            }
                        }
                        if (IsTaken){
                            getQuizInfo(RefToQuizInfo);
                        }
                    }
                }
                else{
                    Toast.makeText(JoinQuiz.this, "Welcome", Toast.LENGTH_SHORT).show();
                    getQuizInfo(RefToQuizInfo);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgress.stopProgressBar();
                Log.d(TAG , "Error Encountered");
                Toast.makeText(JoinQuiz.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void CheckIfUserIsAllowedForQuiz(){}

    private void CheckIfUserIsBlockedForQuiz(){}

    private void getQuizInfo(final DatabaseReference ref) {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                quizBasicInfoData = snapshot.getValue(QuizBasicInfoData.class);
                Size = snapshot.child("Size").getValue(long.class);
                Log.d(TAG , "Getting Data From Quiz Info");
                ToNextActivity(quizBasicInfoData , Size);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgress.stopProgressBar();
                Toast.makeText(JoinQuiz.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG , "Error : " + error.toString());
            }
        });

    }

    private void ToNextActivity(QuizBasicInfoData quizBasicInfoData1 , Long TSize){
        customProgress.stopProgressBar();
        Log.d(TAG , "To Next Screen");
        Intent intent = new Intent(getApplicationContext() , GetIdentifier.class);
        intent.putExtra("ID" , ID);
        intent.putExtra("QuizInfoBasic" , quizBasicInfoData1);
        intent.putExtra("Size" , TSize);
        startActivity(intent);
    }
}