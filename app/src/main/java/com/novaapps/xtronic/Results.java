package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.RecycleViewClasses.ResultsRecycleView;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.util.ArrayList;
import java.util.Objects;

public class Results extends AppCompatActivity {

    //String
    public static final String TAG = "Results";
    ArrayList<String> CreatedQuiz ;
    ArrayList<String> JoinedQuiz ;
    boolean HasCreatedQuiz ;
    boolean HasJoinedQuiz ;
    String NextActivityName;


    //XmlHooks
    RecyclerView recyclerView;
    Button QuizCreatedButton , QuizJoinedButton ;
    ProgressBar progressBar ;
    TextView QuizStatus ;

    //Object
    UI_Data uiData ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.ResultsStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_results);
        XmlHooks();
        ObjectHooks();
        VariableInitializer();


        getQuizCreatedListFromDatabase();

        QuizCreatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizCreatedButton.setBackgroundResource(R.drawable.simple_selected_darkgreen_button);
                QuizJoinedButton.setBackgroundResource(R.drawable.simple_unselected_darkgreen_button);
                NextActivityName = "StudentList";
                HideRecycleView();
                getQuizCreatedListFromDatabase();
            }
        });

        QuizJoinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizCreatedButton.setBackgroundResource(R.drawable.simple_unselected_darkgreen_button);
                QuizJoinedButton.setBackgroundResource(R.drawable.simple_selected_darkgreen_button);
                NextActivityName = "QuizStats";
                HideRecycleView();
                getQuizJoinedListFromDatabase();
            }
        });

    }

    private void XmlHooks(){

        QuizCreatedButton = findViewById(R.id.quizCreatedButton);
        QuizJoinedButton = findViewById(R.id.quizJoinedButton);
        recyclerView = findViewById(R.id.resultRecycleView);
        progressBar = findViewById(R.id.resultsProgressBar);
        QuizStatus = findViewById(R.id.resultsQuizFoundStatus);

    }

    private void VariableInitializer(){
        CreatedQuiz = new ArrayList<>();
        JoinedQuiz = new ArrayList<>();
        HasCreatedQuiz = false ;
        HasJoinedQuiz = false ;
        NextActivityName = "StudentList";
    }

    private void ObjectHooks(){
        uiData = new UI_Data(getApplicationContext());
    }

    private void StartProgressBar(){
            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG , "Progress ON");
    }

    private void StopProgressBar(){
        progressBar.setVisibility(View.GONE);
        Log.d(TAG , "Progress Off");
    }

    private void ToggleQuizFoundStatus(String _status){
        if (_status == null){
            QuizStatus.setVisibility(View.GONE);
        }
        else{
            QuizStatus.setVisibility(View.VISIBLE);
            QuizStatus.setText(_status);
        }
    }

    private void getQuizCreatedListFromDatabase(){
        HasJoinedQuiz = false ;
        StartProgressBar();
        ToggleQuizFoundStatus(null);

        DatabaseReference RefToQuizCreated = FirebaseDatabase.getInstance().getReference("User").child(uiData.get_UserUid()).child("QuizCreated");
        Log.d(TAG , "Connecting to Database..");

        RefToQuizCreated.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long countCreated = snapshot.child("Count").getValue(Long.class);
                Log.d(TAG , "Total Quiz Created : " + countCreated);
                if (countCreated != null) {
                    if (countCreated >= 1){
                        HasCreatedQuiz = true ;
                        ArrayList<String> CreatedIds = new ArrayList<>();
                        for (int i=1 ; i<=countCreated ; i++){
                            String IdOfCreation = snapshot.child(String.valueOf(i)).getValue(String.class);
                            CreatedIds.add(IdOfCreation);
                            Log.d(TAG , "ID " + i + " Found");
                        }
                        Log.d(TAG , "Updating RecycleView List");
                        getCreatedIdTopicAndDate(CreatedIds);

                    }
                    else {
                        StopProgressBar();
                        ToggleQuizFoundStatus("No Quiz Created yet");
                        HasCreatedQuiz = false;
                        HideRecycleView();
                    }
                } else {
                    StopProgressBar();
                    ToggleQuizFoundStatus("Error Found");
                    Log.d(TAG , "Count is Null : Failed Unboxing");
                    Toast.makeText(Results.this, "Failure", Toast.LENGTH_SHORT).show();
                    HideRecycleView();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG , error.toException().toString());
                Toast.makeText(Results.this, error.toString(), Toast.LENGTH_SHORT).show();
                HideRecycleView();
            }
        });

    }

    private void getQuizJoinedListFromDatabase(){
        HasCreatedQuiz = false ;
        StartProgressBar();
        ToggleQuizFoundStatus(null);

        DatabaseReference RefToQuizJoined = FirebaseDatabase.getInstance().getReference("User").child(uiData.get_UserUid()).child("QuizJoined");
        Log.d(TAG , "Connecting to Database..");

        RefToQuizJoined.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long countJoined = snapshot.child("Count").getValue(Long.class);
                Log.d(TAG , "Total Quiz Joined : " + countJoined);
                if (countJoined != null) {
                    if (countJoined >= 1){
                        HasJoinedQuiz = true ;
                        ArrayList<String> JoinedIds = new ArrayList<>();
                        for (int i=1 ; i<=countJoined ; i++){
                            String IdOfJoining = snapshot.child(String.valueOf(i)).getValue(String.class);
                            JoinedIds.add(IdOfJoining);
                            Log.d(TAG , "ID " + i + " Found");
                        }
                        Log.d(TAG , "Updating RecycleView List");
                        getCreatedIdTopicAndDate(JoinedIds);

                    }
                    else {
                        StopProgressBar();
                        ToggleQuizFoundStatus("no Quiz Joined Yet");
                        HasJoinedQuiz = false;
                    }
                } else {
                    StopProgressBar();
                    ToggleQuizFoundStatus("Error Found");
                    Log.d(TAG , "Count is Null : Failed Unboxing");
                    Toast.makeText(Results.this, "Failure", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StopProgressBar();
                ToggleQuizFoundStatus("Error Found");
                Log.d(TAG , error.toException().toString());
                Toast.makeText(Results.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCreatedIdTopicAndDate(final ArrayList<String> IDList){

        DatabaseReference RefToQuizInfoQuizInfoTopicName = FirebaseDatabase.getInstance().getReference("QuizInfo");


            RefToQuizInfoQuizInfoTopicName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ArrayList<QuizBasicInfoData> quizBasicInfoDataArrayList = new ArrayList<>();
                    QuizBasicInfoData quizBasicInfoData = new QuizBasicInfoData();
                   for (int i = 1 ; i <=IDList.size() ; i++){
                       quizBasicInfoData = snapshot.child(IDList.get(i-1)).getValue(QuizBasicInfoData.class);
                       quizBasicInfoDataArrayList.add(quizBasicInfoData);

                   }
                   UpdateList(IDList , quizBasicInfoDataArrayList);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    StopProgressBar();
                    ToggleQuizFoundStatus("Error Found");
                    Log.d(TAG , error.toException().toString());
                }
            });
    }

    private void HideRecycleView(){
        recyclerView.setVisibility(View.GONE);
    }

    private void ShowRecycleView(){
        recyclerView.setVisibility(View.VISIBLE);
    }

    private  void UpdateList(ArrayList<String> IDList ,ArrayList<QuizBasicInfoData> quizBasicInfoDataArrayList){
        StopProgressBar();
        ShowRecycleView();
        ResultsRecycleView resultsRecycleView = new ResultsRecycleView(getApplicationContext() , IDList , quizBasicInfoDataArrayList, NextActivityName);
        recyclerView.setAdapter(resultsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}