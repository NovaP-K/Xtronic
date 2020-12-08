package com.novaapps.xtronic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;
import com.novaapps.xtronic.helpingclass.QuizQuestions;
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.util.ArrayList;
import java.util.Objects;

public class AnswerSheet extends AppCompatActivity {

    //XmlHooks
    Button NextQuestionButton , PrevQButton;
    TextView QuestionTextView , QuestionStatus;
    Button Opt1Button , Opt2Button ,Opt3Button ,Opt4Button ;

    //Variable
    int Index ;
    int IndexSize ;
    String Opt1Text , Opt2Text , Opt3Text , Opt4Text ;
    String CorrectAns ;
    private boolean IsCorrect = false ;
    private boolean ToNxtQuestion = true ;
    private boolean ToPrevQuestion = false ;

    //Objects
    ArrayList<QuizQuestions> questionsArrayList = new ArrayList<QuizQuestions>();
    QuizQuestions quizQuestions;
    ArrayList<String> UserAnswer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.AnswerSheetStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_answer_sheet);
        XmlHookUp();
        ObjectHooks();
        VariableInitialization();
        DisableAllButton();
        ReloadQuestions();

        NextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Index <= IndexSize) {
                    ToNxtQuestion = true ;
                    ToPrevQuestion = false ;
                    ReloadQuestions();
                }
                else
                    Toast.makeText(AnswerSheet.this, "This was Last", Toast.LENGTH_SHORT).show();
            }
        });
        PrevQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Index >= 0) {
                    ToNxtQuestion = false;
                    ToPrevQuestion = true;
                    ReloadQuestions();
                }
                else
                    Toast.makeText(AnswerSheet.this, "This is First", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void XmlHookUp(){
        QuestionTextView = findViewById(R.id.QuestionAnswerSheet);
        Opt1Button = findViewById(R.id.Option1AnswerSheet);
        Opt2Button = findViewById(R.id.Option2AnswerSheet);
        Opt3Button = findViewById(R.id.Option3AnswerSheet);
        Opt4Button = findViewById(R.id.Option4AnswerSheet);
        NextQuestionButton = findViewById(R.id.NextQuestionAnswerSheet);
        PrevQButton = findViewById(R.id.PreviousQuestionAnswerSheet);
        QuestionStatus = findViewById(R.id.QuestionsValidationStatus);
    }
    private void ObjectHooks(){
        Bundle args = getIntent().getBundleExtra("QuestionBank");
        questionsArrayList = (ArrayList<QuizQuestions>) args.getSerializable("QuizQuestion");
        UserAnswer = args.getStringArrayList("QuizAnswers");
    }
    private void VariableInitialization(){
        Index = -1;
        IndexSize = questionsArrayList.size() ;
    }
    private void ReloadQuestions(){

        if (ToPrevQuestion)
            Index--;
        if (ToNxtQuestion)
            Index++;


        quizQuestions = questionsArrayList.get(Index);

        QuestionTextView.setText(quizQuestions.getQuestion());

        Opt1Text = quizQuestions.getOption1();
        Opt2Text = quizQuestions.getOption2();
        Opt3Text = quizQuestions.getOption3();
        Opt4Text = quizQuestions.getOption4();
        CorrectAns = quizQuestions.getANS();

        SetDefaultOptionColor();
        CheckIfQuestionIsCorrect();
        ToggleNullOptionVisibility();

        ToggleCorrectAnswerColor();
        if (!IsCorrect){
            ToggleWrongAnswerColor();
        }
        SetQuestionValidationStatus();

        Opt1Button.setText(Opt1Text);
        Opt2Button.setText(Opt2Text);
        Opt3Button.setText(Opt3Text);
        Opt4Button.setText(Opt4Text);

        if (Index == 0)
            PrevQButton.setVisibility(View.INVISIBLE);
        else{
            PrevQButton.setVisibility(View.VISIBLE);
        }

        if(Index == IndexSize-1)
            NextQuestionButton.setVisibility(View.INVISIBLE);
        else{
            NextQuestionButton.setVisibility(View.VISIBLE);
        }


    }
    private void ToggleNullOptionVisibility(){
        if(!Opt1Text.isEmpty())
            Opt1Button.setVisibility(View.VISIBLE);
        else
            Opt1Button.setVisibility(View.GONE);

        if(!Opt2Text.isEmpty())
            Opt2Button.setVisibility(View.VISIBLE);
        else
            Opt2Button.setVisibility(View.GONE);

        if(!Opt3Text.isEmpty())
            Opt3Button.setVisibility(View.VISIBLE);
        else
            Opt3Button.setVisibility(View.GONE);

        if(!Opt4Text.isEmpty())
            Opt4Button.setVisibility(View.VISIBLE);
        else
            Opt4Button.setVisibility(View.GONE);
    }
    private void CheckIfQuestionIsCorrect(){
        IsCorrect = CorrectAns.equals(UserAnswer.get(Index));
    }
    private void SetDefaultOptionColor(){
        //Option1
        Opt1Button.setBackgroundResource(R.drawable.simple_white_button);
        Opt1Button.setTextColor(getResources().getColor(R.color.BlackColor));
        //Option2
        Opt2Button.setBackgroundResource(R.drawable.simple_white_button);
        Opt2Button.setTextColor(getResources().getColor(R.color.BlackColor));
        //Option3
        Opt3Button.setBackgroundResource(R.drawable.simple_white_button);
        Opt3Button.setTextColor(getResources().getColor(R.color.BlackColor));
        //Option4
        Opt4Button.setBackgroundResource(R.drawable.simple_white_button);
        Opt4Button.setTextColor(getResources().getColor(R.color.BlackColor));
    }
    private void ToggleCorrectAnswerColor(){
        if (getCorrectOptionName().equals("Option1")){
            Opt1Button.setBackgroundResource(R.drawable.correct_ans_button);
            Opt1Button.setTextColor(getResources().getColor(R.color.WhiteColor));
        }
        else
            if (getCorrectOptionName().equals("Option2")){
                Opt2Button.setBackgroundResource(R.drawable.correct_ans_button);
                Opt2Button.setTextColor(getResources().getColor(R.color.WhiteColor));
            }
            else
                if (getCorrectOptionName().equals("Option3")){
                    Opt3Button.setBackgroundResource(R.drawable.correct_ans_button);
                    Opt3Button.setTextColor(getResources().getColor(R.color.WhiteColor));
                }
                else{
                    Opt4Button.setBackgroundResource(R.drawable.correct_ans_button);
                    Opt4Button.setTextColor(getResources().getColor(R.color.WhiteColor));
                }
    }
    private void ToggleWrongAnswerColor(){
        if (getWrongOptionName().equals("Option1")){
            Opt1Button.setBackgroundResource(R.drawable.wrong_ans_button);
            Opt1Button.setTextColor(getResources().getColor(R.color.WhiteColor));
        }
        else
        if (getWrongOptionName().equals("Option2")){
            Opt2Button.setBackgroundResource(R.drawable.wrong_ans_button);
            Opt2Button.setTextColor(getResources().getColor(R.color.WhiteColor));
        }
        else
        if (getWrongOptionName().equals("Option3")){
            Opt3Button.setBackgroundResource(R.drawable.wrong_ans_button);
            Opt3Button.setTextColor(getResources().getColor(R.color.WhiteColor));
        }
        else{
            Opt4Button.setBackgroundResource(R.drawable.wrong_ans_button);
            Opt4Button.setTextColor(getResources().getColor(R.color.WhiteColor));
        }
    }
    private void SetQuestionValidationStatus(){
        if (IsCorrect){
            String txt = "Correct Answer";
            QuestionStatus.setBackgroundResource(R.drawable.correct_q_status);
            QuestionStatus.setText(txt);
            QuestionStatus.setTextColor(getResources().getColor(R.color.CorrectQColor));
            QuestionStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.check) , null , null , null);
        }
        else{
            String txt = "Wrong Answer";
            QuestionStatus.setBackgroundResource(R.drawable.wrong_q_status);
            QuestionStatus.setText(txt);
            QuestionStatus.setTextColor(getResources().getColor(R.color.WrongQColor));
            QuestionStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.close_outline) , null , null , null);
        }
    }
    private String getCorrectOptionName(){
        if (CorrectAns.equals(Opt1Text))
            return "Option1";
        if (CorrectAns.equals(Opt2Text))
            return "Option2";
        if (CorrectAns.equals(Opt3Text))
            return "Option3";
        else
            return "Option4";
    }
    private String getWrongOptionName(){
        if (UserAnswer.get(Index).equals(Opt1Text))
            return "Option1";
        if (UserAnswer.get(Index).equals(Opt2Text))
            return "Option2";
        if (UserAnswer.get(Index).equals(Opt3Text))
            return "Option3";
        else
            return "Option4";
    }
    private void DisableAllButton(){
        Opt1Button.setClickable(false);
        Opt2Button.setClickable(false);
        Opt3Button.setClickable(false);
        Opt4Button.setClickable(false);
    }
}