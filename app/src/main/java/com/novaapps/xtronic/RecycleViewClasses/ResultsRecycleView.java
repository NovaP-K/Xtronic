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
import com.novaapps.xtronic.helpingclass.UI_Data;

import java.util.ArrayList;

public class ResultsRecycleView extends RecyclerView.Adapter<ResultsRecycleView.ResultsViewHolder>{

    ArrayList<String> IDList;
    ArrayList<QuizBasicInfoData> quizBasicInfoDataArrayList ;
    Context context ;
    UI_Data uiData ;

    String NextActivityName ;

    public ResultsRecycleView(Context ct , ArrayList<String> IDValues , ArrayList<QuizBasicInfoData> dataArrayList, String ActivityName){
        if (IDValues == null)
            IDList.clear();
        else
            IDList = IDValues ;

        if (dataArrayList == null && quizBasicInfoDataArrayList != null)
            quizBasicInfoDataArrayList.clear();
        else
            quizBasicInfoDataArrayList = dataArrayList ;

       NextActivityName = ActivityName ;
       context = ct ;
        uiData = new UI_Data(context);
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.results_recycleview , parent , false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, final int position) {

            final QuizBasicInfoData quizBasicInfoData = quizBasicInfoDataArrayList.get(position);

              holder.QuizCode.setText("#" + IDList.get(position));
              holder.QuizTopic.setText(quizBasicInfoData.get_TopicName());
              holder.QuizDate.setText(quizBasicInfoData.get_DateCreated());

              holder.resultsCardView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      ToNextActivity(IDList.get(position) , uiData.get_UserUid() , quizBasicInfoData);
                  }
              });

    }

    @Override
    public int getItemCount() {
        return IDList.size();
    }

    public class ResultsViewHolder extends RecyclerView.ViewHolder {

        TextView QuizCode, QuizDate, QuizTopic;
        CardView resultsCardView;

        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            QuizCode = itemView.findViewById(R.id.resultsQuizCode);
            QuizDate = itemView.findViewById(R.id.resultsDate);
            QuizTopic = itemView.findViewById(R.id.resultsQuizTopic);
            resultsCardView = itemView.findViewById(R.id.resultsListCardView);

        }
    }

    private void ToNextActivity(String ID , String LoggedInUserUid , QuizBasicInfoData infoData){
        Intent intent;
        if (NextActivityName.equals("QuizStats")) {
            intent = new Intent(context, QuizStats.class);
            intent.putExtra("UserUid" , LoggedInUserUid);
        }
        else
            intent = new Intent(context , StudentList.class);

        intent.putExtra("QuizInfo" , infoData);
        intent.putExtra("ID" , ID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
