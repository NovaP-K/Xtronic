package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.novaapps.xtronic.helpingclass.CustomProgress;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;
import com.novaapps.xtronic.helpingclass.QuizQuestions;
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class QuizTime extends AppCompatActivity {

    //XmlHooks
    Button NextQuestionButton;
    TextView QuestionTextView ;
    Button Opt1Button , Opt2Button ,Opt3Button ,Opt4Button ;
    TextView TimerView ;
    View progressView ;

    //Variable
    int Score ;
    String ID ;
    int Index ;
    int IndexSize ;
    long TimeLeftInMillis ;
    private boolean IsQuizSubmitted = false;
    private boolean QuizRunningStatus = true ;
    String Opt1Text , Opt2Text , Opt3Text , Opt4Text ;
    private String CorrectAns ;
    private static final String TAG = "QuizTime" ;
    int reqCode = 1 ;


    //Objects
    CountDownTimer countDownTimer ;
    ArrayList<QuizQuestions> questionsArrayList = new ArrayList<QuizQuestions>();
    QuizQuestions quizQuestions;
    QuizBasicInfoData quizBasicInfoData ;
    ArrayList<String> UserAnswer = new ArrayList<String>();
    UI_Data uiData ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.QuizTimeStatus));   //Set Status Bar Color
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);  //Disable ScreenShot
        setContentView(R.layout.activity_quiz_time);
        XmlHookUp();
        ObjectHooks();
        VariableInitialization();
        ReloadQuestions();
        SetAndStartTimer();
        SetDefaultNotification();


        NextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextQuestion();
            }
        });

        Opt1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAnswer(Opt1Text);
                NextQuestion();
            }
        });

        Opt2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAnswer(Opt2Text);
                NextQuestion();
            }
        });

        Opt3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAnswer(Opt3Text);
                NextQuestion();
            }
        });

        Opt4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAnswer(Opt4Text);
                NextQuestion();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!IsQuizSubmitted) {
            QuizRunningStatus = false;  //Activity is in Background


            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    CountDownTimer countDownTimer = new CountDownTimer(10000 , 1000) {
                        @Override
                        public void onTick(long l) {
                            String msg = "Return to Quiz in " + ((l / 1000) % 60) ;
                            if (!QuizRunningStatus)
                                showNotification(getApplicationContext(), "Xtronic", msg , reqCode , false);
                            else {
                                cancel();
                                SetDefaultNotification();
                            }
                        }

                        @Override
                        public void onFinish() {
                            if (!QuizRunningStatus)
                               CloseQuiz();
                        }
                    }.start();
                }
            });

        }
    }

    @Override
    protected void onResume() {

        QuizRunningStatus = true;  //Activity is Running Again
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set Button
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        // Set other dialog properties
        builder.setCancelable(false);
        //Set Dialog Msg
        builder.setMessage("Once you exit the Quiz , You can not take the Quiz again ");

        builder.setTitle("Caution");
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void CloseQuiz(){

        showNotification(getApplicationContext() , "Xtronic" , "Quiz Closed" , reqCode , true);
        finish();
    }
    private void SetDefaultNotification(){
        showNotification(getApplicationContext() , "Xtronic" , "Quiz is in Progress" , reqCode , false);
    }
    public void showNotification(Context context, String title, String message, int reqCode , boolean toDismiss) {

        String CHANNEL_ID = "Xtronic Quiz"; // The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.xtronic_ico)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Xtronic";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        if (toDismiss)
            notificationManager.cancel(reqCode);
        else
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

    private void XmlHookUp(){
        QuestionTextView = findViewById(R.id.QuestionHere);
        Opt1Button = findViewById(R.id.Option1);
        Opt2Button = findViewById(R.id.Option2);
        Opt3Button = findViewById(R.id.Option3);
        Opt4Button = findViewById(R.id.Option4);
        TimerView = findViewById(R.id.Timer);
        NextQuestionButton = findViewById(R.id.NextQuestion);
        progressView = findViewById(R.id.progressViewQuizTime);
    }

    private void ObjectHooks(){
        quizBasicInfoData = (QuizBasicInfoData) getIntent().getSerializableExtra("QuizInfoBasic");
        Bundle args = getIntent().getBundleExtra("QuestionArray");
        questionsArrayList = (ArrayList<QuizQuestions>) args.getSerializable("QuizQuestionsBundle");
        quizQuestions = new QuizQuestions();
        uiData = new UI_Data(getApplicationContext());
    }

    private void VariableInitialization(){
        Index = 0;
        IndexSize = questionsArrayList.size()-1 ;
        Score = 0;
        ID = getIntent().getStringExtra("ID");
        TimeLeftInMillis = quizBasicInfoData.get_Duration() * 60000;
    }

    private void ValidateAnswer(String SelectedAnswer){

        if (CorrectAns.equals(SelectedAnswer)){
            ++Score ;
        }

        UserAnswer.add(SelectedAnswer);


    }

    private void ReloadQuestions(){
         quizQuestions = questionsArrayList.get(Index);

         QuestionTextView.setText(quizQuestions.getQuestion());

         Opt1Text = quizQuestions.getOption1();
         Opt2Text = quizQuestions.getOption2();
         Opt3Text = quizQuestions.getOption3();
         Opt4Text = quizQuestions.getOption4();
         CorrectAns = quizQuestions.getANS();


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

        Opt1Button.setText(Opt1Text);
        Opt2Button.setText(Opt2Text);
        Opt3Button.setText(Opt3Text);
        Opt4Button.setText(Opt4Text);

    }

    private void NextQuestion(){

        if (Index < IndexSize){
            Index++;
            ReloadQuestions();
        }
        else
        {
            UploadAnswerSheet();
        }
    }

    private void SetAndStartTimer(){
        if (quizBasicInfoData.get_Duration() == 0){
            TimerView.setText("--:--");
        }else {
            if (quizBasicInfoData.get_IsQuestionBasedTimer()){
                final long tempTimer = TimeLeftInMillis ;
                countDownTimer = new CountDownTimer(tempTimer, 1000) {
                    @Override
                    public void onTick(long l) {
                        if (isGAssistantRunning())
                            CreateMsgAlert("You are not Allowed to Use Google Assistant during the Quiz. \n The Quiz will be closed in few Seconds");
                        UpdateCountdownText(l);
                    }

                    @Override
                    public void onFinish() {
                        NextQuestion();
                    }
                }.start();
            }
            countDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
                @Override
                public void onTick(long l) {
                    if (isGAssistantRunning())
                        CreateMsgAlert("You are not Allowed to Use Google Assistant during the Quiz. \n The Quiz will be closed in few Seconds");
                    TimeLeftInMillis = l;
                    UpdateCountdownText(TimeLeftInMillis);
                }

                @Override
                public void onFinish() {
                    UploadAnswerSheet();
                }
            }.start();
        }
    }

    private void UpdateCountdownText(long Left){
        int min = (int) (Left / 1000) / 60 ;
        int sec = (int) (Left / 1000) % 60 ;
        String timeLeftFormatted = String.format(Locale.getDefault() , "%02d:%02d" , min , sec );
        if(min == 0 ){
            TimerView.setTextColor(getResources().getColor(R.color.redColor));
        }

        TimerView.setText(timeLeftFormatted);

    }

    private void  UploadAnswerSheet(){
        DatabaseReference RefToUserAnswer = FirebaseDatabase.getInstance().getReference("QuizAnswer").child(ID).child(uiData.get_UserUid()).child("Answers");
        DatabaseReference RefToUserScore = FirebaseDatabase.getInstance().getReference("QuizAnswer").child(ID).child(uiData.get_UserUid()).child("Score");
        DatabaseReference RefToUserTotalScore = FirebaseDatabase.getInstance().getReference("QuizAnswer").child(ID).child(uiData.get_UserUid()).child("Total Score");
        final CustomProgress customProgress = new CustomProgress(getApplicationContext() , progressView);

        customProgress.startProgressBar();

        for (int i = 0 ; i < UserAnswer.size() ; i++) {
            RefToUserAnswer.child(String.valueOf(i + 1)).setValue(UserAnswer.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {}
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(QuizTime.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        RefToUserScore.setValue(Score).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
                Toast.makeText(QuizTime.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RefToUserTotalScore.setValue(IndexSize+1).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
                Toast.makeText(QuizTime.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        customProgress.stopProgressBar();
        IsQuizSubmitted = true ;
        ToNextActivity();

   }

   private void ToNextActivity(){
       Intent intent = new Intent(getApplicationContext() , QuizSubmission.class);
       intent.putExtra("Score" , Score) ;
       intent.putExtra("Total" , IndexSize+1);
       startActivity(intent);
       finish();
   }
    private void CreateMsgAlert(String Msg){
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
    public  boolean isGAssistantRunning( ) {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals("com.google.android.apps.googleassistant")) {
                    return true;
                }
            }
        }
        return false;
    }

}