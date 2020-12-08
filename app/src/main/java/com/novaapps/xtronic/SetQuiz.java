package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novaapps.xtronic.helpingclass.CustomProgress;
import com.novaapps.xtronic.helpingclass.QuizQuestions;
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.util.ArrayList;
import java.util.Objects;

public class SetQuiz extends AppCompatActivity {

    //XML_Hooks
    RadioButton Option1 , Option2 , Option3 , Option4 ;
    EditText QuestionText , Option1Text , Option2Text , Option3Text , Option4Text ;
    RelativeLayout Option1Layout , Option2Layout , Option3Layout , Option4Layout ;
    ConstraintLayout AddOption ;
    Button AddQuestion , SetQuizButton;
    View progressView ;

    //Variable
    String ID ;
    int Qno = 1;
    String Question ;
    String Opt1 ;
    String Opt2 ;
    String Opt3 ;
    String Opt4 ;
    String Ans = "";
    int TotalOptions = 0;
    boolean IsQuestionAdded = false ;

    //Objects
    ArrayList<QuizQuestions> questionsArrayList = new ArrayList<QuizQuestions>();
    UI_Data ui_data ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.SetQuizStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_set_quiz);
        XmlHookUp();
        ObjHooks();

        SetQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext() , QuizSharing.class));
            }
        });

        Option1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                OnCheckOption1(compoundButton);
            }
        });


        Option2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                OnCheckOption2(compoundButton);
            }
        });

        Option3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                OnCheckOption3(compoundButton);
            }
        });

        Option4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                OnCheckOption4(compoundButton );
            }
        });

        AddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOption();
            }
        });

        AddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckDuplicateOption()) {
                    AddQuestionToList();
                    if (IsQuestionAdded)
                        ReloadQuizView();
                }
                else
                    Toast.makeText(SetQuiz.this, "Duplicate Option Found", Toast.LENGTH_SHORT).show();
            }
        });

        SetQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckDuplicateOption()) {
                    UploadQuestionsToFirebase();
                }
                else {
                    Toast.makeText(SetQuiz.this, "Duplicate Option Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void XmlHookUp(){

        //RelativeLayout
        Option1Layout = findViewById(R.id.Option1Layout);
        Option2Layout = findViewById(R.id.Option2Layout);
        Option3Layout = findViewById(R.id.Option3Layout);
        Option4Layout = findViewById(R.id.Option4Layout);
        //RadioButton
        Option1 = findViewById(R.id.Option1Button);
        Option2 = findViewById(R.id.Option2Button);
        Option3 = findViewById(R.id.Option3Button);
        Option4 = findViewById(R.id.Option4Button);
        //EditText
        QuestionText = findViewById(R.id.QuizQuestionBox);
        Option1Text = findViewById(R.id.Option1EditText);
        Option2Text = findViewById(R.id.Option2EditText);
        Option3Text = findViewById(R.id.Option3EditText);
        Option4Text = findViewById(R.id.Option4EditText);
        //ConstraintLayout
        AddOption = findViewById(R.id.MoreOptionsButton);
        //Button
        AddQuestion = findViewById(R.id.MoreQuestionsButton);
        SetQuizButton = findViewById(R.id.SetQuizSubmitButton);
        //IncludeView
        progressView = findViewById(R.id.progressViewSetQuiz);
    }

    private void ObjHooks(){
        ID = getIntent().getStringExtra("ID");
        QuizQuestions quizQuestions = new QuizQuestions();
        ui_data = new UI_Data(getApplicationContext());

    }

    private boolean CheckDuplicateOption(){

        String O1 = Option1Text.getText().toString() ;
        String O3 = Option2Text.getText().toString() ;
        String O2 = Option3Text.getText().toString() ;
        String O4 = Option4Text.getText().toString() ;

        if (!O1.isEmpty()){
            if (O1.equals(O2) || O1.equals(O3) || O1.equals(O4))
                return false ;
        }

        if (!O2.isEmpty()){
            if (O2.equals(O1) ||O2.equals(O3) || O2.equals(O4))
                return false ;
        }

        if (!O3.isEmpty()){
            if (O3.equals(O1) || O3.equals(O2) || O3.equals(O4))
                return false ;
        }

        if (!O4.isEmpty()){
            if (O4.equals(O1) || O4.equals(O2) || O4.equals(O3))
                return false ;
        }

        return true ;
    }

    private void AddOption(){

        if (Option3Layout.getVisibility() == View.GONE){
            Option3Layout.setVisibility(View.VISIBLE);
        }
        else if (Option4Layout.getVisibility() == View.GONE){
            Option4Layout.setVisibility(View.VISIBLE);
            AddOption.setVisibility(View.GONE);
        }

    }

    private void OnCheckOption1(CompoundButton compoundButton ){
        if (compoundButton.isChecked()) {
            compoundButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            Option2.setChecked(false);
            Option3.setChecked(false);
            Option4.setChecked(false);
            if (Option1Text.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter the Options First", Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            }
        }
        else
            compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0 ,0 ,0);
    }

    private void OnCheckOption2(CompoundButton compoundButton ){
        if (compoundButton.isChecked()){
            compoundButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            Option1.setChecked(false);
            Option3.setChecked(false);
            Option4.setChecked(false);
            if (Option2Text.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter the Options First", Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0 ,0 ,0);
            }
        }
        else {
            compoundButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void OnCheckOption3(CompoundButton compoundButton ){
        if (compoundButton.isChecked()){
            compoundButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            Option1.setChecked(false);
            Option2.setChecked(false);
            Option4.setChecked(false);

            if (Option3Text.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter the Options First", Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0 ,0 ,0);
            }
        }
        else {
            compoundButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void OnCheckOption4(CompoundButton compoundButton ){
        if (compoundButton.isChecked()){
            compoundButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            Option1.setChecked(false);
            Option2.setChecked(false);
            Option3.setChecked(false);
            if (Option4Text.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter the Options First", Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0 ,0 ,0);
            }
        }
        else {
            compoundButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void ReloadQuizView(){
        if (IsQuestionAdded) {
            SetAllNull();

            //Next Qno
            ++Qno;

            IsQuestionAdded = false ;
        }
    }

    private void SetAllNull(){

        //SetAllInputNull
        QuestionText.setText(null);
        Option1Text.setText(null);
        Option2Text.setText(null);
        Option3Text.setText(null);
        Option4Text.setText(null);
        //SetCheckBoxFalse
        Option1.setChecked(false);
        Option2.setChecked(false);
        Option3.setChecked(false);
        Option4.setChecked(false);

    }

    private void AssignQuizValues(){

            Question = QuestionText.getText().toString();
            Opt1 = Option1Text.getText().toString();
            Opt2 = Option2Text.getText().toString();
            Opt3 = Option3Text.getText().toString();
            Opt4 = Option4Text.getText().toString();
            Ans = getCorrectAns();

    }

    private String getCorrectAns(){

        if (Option1.isChecked())
            return Opt1;
        if (Option2.isChecked())
            return Opt2;
        if (Option3.isChecked())
            return Opt3;
        if (Option4.isChecked())
            return Opt4;

        return null;

    }

    private boolean CheckForError(){

        //Questions
        if (Question.isEmpty()){
            Toast.makeText(this, "You Forget the Question !!", Toast.LENGTH_SHORT).show();
            return false;
        }


        //Options
        if (TotalOptions < 2){
            Toast.makeText(this, "At Least Two options Required", Toast.LENGTH_SHORT).show();
            return false;
        }


        //Correct Option
        if (!Option1.isChecked() && !Option2.isChecked() && !Option3.isChecked() && !Option4.isChecked()) {
                Toast.makeText(this, "You need to Select the Correct Answer too", Toast.LENGTH_SHORT).show();
                return false;
            }


        return true;

    }

    private void CheckTotalOptions(){
        TotalOptions = 0;

        if (!Opt1.isEmpty())
            TotalOptions++;

        if (!Opt2.isEmpty())
            TotalOptions++;

        if (!Opt3.isEmpty())
            TotalOptions++;

        if (!Opt4.isEmpty())
            TotalOptions++;

    }


    private void UploadQuestionsToFirebase(){
        if(IsQuestionAdded) {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference RefToQues = mDatabase.getReference("QuestionBank");
            DatabaseReference RefToQuesInfoSize = mDatabase.getReference("QuizInfo").child(ID);

            final CustomProgress customProgress = new CustomProgress(getApplicationContext() , progressView);

            customProgress.startProgressBar();

            RefToQuesInfoSize.child("Size").setValue((long) questionsArrayList.size()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetQuiz.this, "Error 1: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });


            RefToQues.child(ui_data.get_UserUid()).child(ID).setValue(questionsArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    customProgress.stopProgressBar();
                    SetAllNull();
                    ToNextActivity();
                }
            }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgress.stopProgressBar();
                        Toast.makeText(SetQuiz.this, "Error 2: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });;

        }
        else {
            AddQuestionToList();
            if (CheckForError()) {
                UploadQuestionsToFirebase();
            }
        }

    }

    private void ToNextActivity(){
        Intent intent = new Intent(getApplicationContext() , QuizSharing.class);
        intent.putExtra("ID" , ID);
        startActivity(intent);
        finish();
    }

    private void AddQuestionToList(){
        AssignQuizValues();
        CheckTotalOptions();

        if (CheckForError()){
            QuizQuestions questions = new QuizQuestions();

            //Add  Question to Objects
            questions.setQno(Qno);
            questions.setQuestion(Question);
            questions.setOption1(Opt1);
            questions.setOption2(Opt2);
            questions.setOption3(Opt3);
            questions.setOption4(Opt4);
            questions.setANS(Ans);

            //Add Question to List
            questionsArrayList.add(questions);
            // Question Added To SQL Status
            IsQuestionAdded = true ;
        }
    }

}