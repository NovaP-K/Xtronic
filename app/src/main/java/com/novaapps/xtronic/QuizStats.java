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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class QuizStats extends AppCompatActivity {

    String ID ;
    String UserUid ;

    //Objects
    QuizBasicInfoData quizBasicInfoData ;
    CustomProgress customProgress ;
    UI_Data uiData ;

    //XmlHooks
    TextView QuizIDTxtView , IdentifierTxtView , UserNameTxtView , TopicTxtView , CreatorTxtView , DurationTxtView , CorrectQTxtView , TotalQTxtView;
    Button ViewAnswerSheetButton ;
    LinearLayout QuizDetailsLayout ;
    View progressView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.QuizStatsStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_quiz_stats);
        XmlHooks();

        ID = getIntent().getStringExtra("ID");
        UserUid = getIntent().getStringExtra("UserUid");
        quizBasicInfoData = (QuizBasicInfoData) getIntent().getSerializableExtra("QuizInfo");
        customProgress = new CustomProgress(getApplicationContext() , progressView);
        uiData = new UI_Data(getApplicationContext());

        LoadDataOfID();
        ToggleAnswerSheetButton();

        ViewAnswerSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadQuestionBank();
            }
        });

    }

    private void XmlHooks(){
        QuizIDTxtView = findViewById(R.id.statsQuizID);
        IdentifierTxtView = findViewById(R.id.statsIdentifier);
        UserNameTxtView = findViewById(R.id.statsUsername);
        TopicTxtView = findViewById(R.id.statsTopicName);
        CreatorTxtView = findViewById(R.id.statsCreatedBy);
        DurationTxtView = findViewById(R.id.statsDuration);
        CorrectQTxtView = findViewById(R.id.statsCorrectQ);
        TotalQTxtView = findViewById(R.id.statsTotalQuestion);
        ViewAnswerSheetButton = findViewById(R.id.ViewAnswerSheetButton);
        QuizDetailsLayout = findViewById(R.id.QuizDetailsLayout);
        progressView = findViewById(R.id.progressViewQuizStats);
    }

    private void LoadDataOfID(){
        DatabaseReference RefToUserAnswerNode = FirebaseDatabase.getInstance().getReference("QuizAnswer").child(ID).child(UserUid);

        RefToUserAnswerNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JoinedUserInfo joinedUserInfo ;

                joinedUserInfo = snapshot.getValue(JoinedUserInfo.class);
                Long Score = snapshot.child("Score").getValue(Long.class);
                Long TotalScore = snapshot.child("Total Score").getValue(Long.class);

                assert joinedUserInfo != null;
                updateView(joinedUserInfo , Score , TotalScore);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizStats.this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ToggleAnswerSheetButton(){
        ViewAnswerSheetButton.setVisibility(View.GONE);
        ViewAnswerSheetButton.setClickable(false);

        if(uiData.get_UserUid().equals(quizBasicInfoData.get_CreatorUid())){
            ViewAnswerSheetButton.setVisibility(View.VISIBLE);
            ViewAnswerSheetButton.setClickable(true);
        }

    }

    private void LoadQuestionBank(){
        customProgress.startProgressBar();
        DatabaseReference RefToQuestionBank = FirebaseDatabase.getInstance().getReference("QuestionBank").child(quizBasicInfoData.get_CreatorUid()).child(ID);

        RefToQuestionBank.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long Count = snapshot.getChildrenCount();
                ArrayList<QuizQuestions> quizQuestionsArrayList = new ArrayList<>();
                for (int i=0 ; i<Count ;i++){
                    QuizQuestions quizQuestions = snapshot.child(String.valueOf(i)).getValue(QuizQuestions.class);
                    quizQuestionsArrayList.add(quizQuestions);
                }
                LoadAnswers(quizQuestionsArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgress.stopProgressBar();
                Log.d(getApplicationContext().toString() , "Error " + error.toString());
            }
        });

    }

    private void LoadAnswers(final ArrayList<QuizQuestions> questionsArrayList){
        DatabaseReference RefToAnswer = FirebaseDatabase.getInstance().getReference("QuizAnswer").child(ID).child(UserUid).child("Answers");

        RefToAnswer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long Count = snapshot.getChildrenCount();
                    ArrayList<String> Answers = new ArrayList<>();
                    for (int i = 1; i <= Count; i++) {
                        String Answer = snapshot.child(String.valueOf(i)).getValue(String.class);
                        Answers.add(Answer);
                    }
                    ToNextActivity(questionsArrayList, Answers);
                }
                else{
                    CreateMsgAlert("User Have not Completed the Quiz");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgress.stopProgressBar();
                Log.d(getApplicationContext().toString() , "Error " + error.toString());
            }
        });

    }

    private void ToNextActivity( ArrayList<QuizQuestions> questionsArrayList , ArrayList<String> AnswersList){
        customProgress.stopProgressBar();

        Intent intent = new Intent(getApplicationContext() , AnswerSheet.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("QuizQuestion", questionsArrayList);
        bundle.putSerializable("QuizAnswers", AnswersList);
        intent.putExtra("QuestionBank",bundle);
        startActivity(intent);
    }

    private void CreateMsgAlert(String Msg){
        customProgress.stopProgressBar();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set other dialog properties
        builder.setCancelable(true);
        //Set Dialog Msg
        builder.setMessage(Msg);
        builder.setTitle("Error");
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateView(JoinedUserInfo userInfo , Long CorrectQ , Long TotalQ){

        QuizIDTxtView.setText(ID);
        IdentifierTxtView.setText(userInfo.get_Identifier());
        UserNameTxtView.setText(userInfo.get_Name());
        TopicTxtView.setText(quizBasicInfoData.get_TopicName());
        CreatorTxtView.setText(quizBasicInfoData.get_CreatorName());
        DurationTxtView.setText(quizBasicInfoData.get_Duration() + " Min");
        CorrectQTxtView.setText(String.valueOf(CorrectQ));
        TotalQTxtView.setText(String.valueOf(TotalQ));

    }


}