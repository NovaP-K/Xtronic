package com.novaapps.xtronic.RecycleViewClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.novaapps.xtronic.QuizStats;
import com.novaapps.xtronic.R;
import com.novaapps.xtronic.StudentList;
import com.novaapps.xtronic.helpingclass.QuizBasicInfoData;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {

    ArrayList<String> UidList = new ArrayList<>();
    ArrayList<String> IdentifierList = new ArrayList<>();
    Context context ;
    String ID ;
    QuizBasicInfoData quizBasicInfoData ;

    public StudentListAdapter(Context ct , ArrayList<String> uidList , ArrayList<String> identifierList  , QuizBasicInfoData infoData , String id) {
        if (uidList == null)
            UidList.clear();
        else
            UidList = uidList ;
        if (identifierList == null)
            IdentifierList.clear();
        else
            IdentifierList = identifierList;

        quizBasicInfoData = infoData ;
        ID = id ;
        context = ct ;
    }

    @NonNull
    @Override
    public StudentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.studentlist_recycleview , parent , false);
        return new StudentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListViewHolder holder, final int position) {
        holder.StudentIdentifier.setText(IdentifierList.get(position));
        holder.IdentifierCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToNextActivity(UidList.get(position));
            }
        });
        holder.StudentIdentifierType.setText(quizBasicInfoData.get_IdentifierType());
    }

    @Override
    public int getItemCount() {
        return UidList.size();
    }

    public class StudentListViewHolder extends RecyclerView.ViewHolder{

        TextView StudentIdentifier ;
        TextView StudentIdentifierType ;
        CardView IdentifierCardView;

        public StudentListViewHolder(@NonNull View itemView) {
            super(itemView);
            StudentIdentifier = itemView.findViewById(R.id.StudentIdentifier);
            StudentIdentifierType = itemView.findViewById(R.id.IdentifierTypeStudentList);
            IdentifierCardView = itemView.findViewById(R.id.IdentifierCardView);
        }
    }

    private void ToNextActivity( String CurrentUserUid ){
        Intent intent;

        intent = new Intent(context, QuizStats.class);
        intent.putExtra("UserUid" , CurrentUserUid);
        intent.putExtra("QuizInfo" , quizBasicInfoData);
        intent.putExtra("ID" , ID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
