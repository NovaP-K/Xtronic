package com.novaapps.xtronic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.RecycleViewClasses.StudentListAdapter;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;

import java.util.ArrayList;
import java.util.Objects;

public class StudentList extends AppCompatActivity {

    String ID ;
    public static final String TAG = "StudentList";

    //XmlHooks
    RecyclerView StudentListView;
    ProgressBar progressBar ;
    TextView StudentListStatus ;

    //Objects
    QuizBasicInfoData infoData ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();                               //Hide Action Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.StudentListStatus));   //Set Status Bar Color
        setContentView(R.layout.activity_student_list);
        XmlHooks();


        ID = getIntent().getStringExtra("ID");
        infoData = (QuizBasicInfoData) getIntent().getSerializableExtra("QuizInfo");

        stopProgress();
        ToggleQuizFoundStatus(null);
        LoadEnrolledUserData();
    }

    private void XmlHooks(){
        StudentListView = findViewById(R.id.studentListRecycleView);
        progressBar = findViewById(R.id.studentListProgressBar);
        StudentListStatus = findViewById(R.id.studentListStatus);
    }

    private void startProgress(){
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG , "Progress On");
    }

    private void stopProgress(){
        progressBar.setVisibility(View.GONE);
        Log.d(TAG , "Progress Off");
    }

    private void ToggleQuizFoundStatus(String _status){
        if (_status == null){
            StudentListStatus.setVisibility(View.GONE);
        }
        else{
            StudentListStatus.setVisibility(View.VISIBLE);
            StudentListStatus.setText(_status);
        }
    }

    private  void LoadEnrolledUserData(){
        startProgress();

        DatabaseReference RefToEnrolledUser = FirebaseDatabase.getInstance().getReference("QuizInfo").child(ID).child("Enrolled Users");

        RefToEnrolledUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> UserList = new ArrayList<>();
                if (snapshot.exists()) {
                    ToggleQuizFoundStatus(null);
                    long Count = snapshot.getChildrenCount();
                    if (Count > 0) {
                        for (int i = 1; i <= Count; i++) {
                            String UserUid = snapshot.child(String.valueOf(i)).getValue(String.class);
                            UserList.add(UserUid);
                        }
                        GetUserIdentifierFromUid(UserList);
                    }
                    else{
                        stopProgress();
                        updateRecycleView(null , null);
                        ToggleQuizFoundStatus("No User Enrolled yet");
                    }
                }
                else{
                    stopProgress();
                    updateRecycleView(null , null);
                    ToggleQuizFoundStatus("No User Enrolled yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stopProgress();
                Log.d(TAG , error.toException().toString());
            }
        });

    }

    private void GetUserIdentifierFromUid(final ArrayList<String> UserUid){

        DatabaseReference RefToStudentInfo = FirebaseDatabase.getInstance().getReference("QuizAnswer").child(ID);

        RefToStudentInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> UserIdentifier  = new ArrayList<>();
                for (int i = 0 ; i<UserUid.size() ; i++){

                    String Identifier = snapshot.child(UserUid.get(i)).child("_Identifier").getValue(String.class);
                    UserIdentifier.add(Identifier);
                }
                updateRecycleView(UserUid , UserIdentifier);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stopProgress();
                Log.d(TAG , error.toException().toString());
            }
        });

    }

    private void updateRecycleView(ArrayList<String> UidList , ArrayList<String> IdentifierList){
        stopProgress();
        StudentListAdapter studentListAdapter = new StudentListAdapter(getApplicationContext() , UidList , IdentifierList , infoData , ID);
        StudentListView.setAdapter(studentListAdapter);
        StudentListView.setLayoutManager(new LinearLayoutManager(this));
    }

}