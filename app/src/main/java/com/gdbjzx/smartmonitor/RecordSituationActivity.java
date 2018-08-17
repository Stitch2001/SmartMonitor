package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecordSituationActivity extends AppCompatActivity {

    private List<mSituation> situationList = new ArrayList<>();

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private int number;
    public int listNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_situation);

        /*设置Toolbar为默认ActionBar，设置图标*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*获取班级并显示*/
        final Intent intent = getIntent();
        number = intent.getIntExtra("number",1);
        int currentGrade = intent.getIntExtra("currentGrade",0);
        int currentRoom = intent.getIntExtra("currentRoom",1);
        TextView classNameView = (TextView) findViewById(R.id.class_name);
        switch (currentGrade){
            case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
            case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
            case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
            case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
            case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
            case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
        }

        /*读取扣分情况*/
        SharedPreferences pref = getSharedPreferences("RecordSituation",MODE_PRIVATE);
        listNum = pref.getInt("listNum"+number,0);
        for (int i = 1;i <= listNum;i++){
            String location = pref.getString("location"+number+""+i,"");
            String event = pref.getString("event"+number+""+i,"");
            int score = pref.getInt("score"+number+""+i,0);
            situationList.add(new mSituation(location,event,score));
        }

        /*导入RecyclerView*/
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.situations);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final SituationAdapter adapter = new SituationAdapter(situationList,RecordSituationActivity.this);
        recyclerView.setAdapter(adapter);

        /*设置点击事件*/
        String[] situations = new String[]
                { "使用手机", "玩手机", "戴耳机", "全班吵闹" };
        ArrayAdapter<String> situationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, situations);
        final EditText locationYText = (EditText) findViewById(R.id.location_y);
        final EditText locationXText = (EditText) findViewById(R.id.location_x);
        final AutoCompleteTextView eventText = (AutoCompleteTextView) findViewById(R.id.event);
        eventText.setAdapter(situationsAdapter);
        final EditText scoreText = (EditText) findViewById(R.id.score);
        final EditText locationText = (EditText) findViewById(R.id.location);
        final EditText personNumberText = (EditText) findViewById(R.id.person_number);
        final AutoCompleteTextView event2Text = (AutoCompleteTextView) findViewById(R.id.event2);
        event2Text.setAdapter(situationsAdapter);
        final EditText score2Text = (EditText) findViewById(R.id.score2);
        FloatingActionButton submitButton = (FloatingActionButton) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*将输入的数据录入到列表中*/
                String locationYString,locationXString,scoreString,score2String;
                locationYString = locationYText.getText().toString();
                locationXString = locationXText.getText().toString();
                scoreString = scoreText.getText().toString();
                score2String = score2Text.getText().toString();
                int locationY,locationX,score,score2;
                if (!locationYString.isEmpty()){
                    locationY = Integer.parseInt(locationYString);
                } else locationY = 0;
                if (!locationXString.isEmpty()){
                    locationX = Integer.parseInt(locationXString);
                } else locationX = 0;
                String event = eventText.getText().toString();
                if (!scoreString.isEmpty()) {
                    score = Integer.parseInt(scoreString);
                } else score = 0;
                String location = locationText.getText().toString();
                String personNumber = personNumberText.getText().toString();
                String event2 = event2Text.getText().toString();
                if (!score2String.isEmpty()) {
                    score2 = Integer.parseInt(score2String);
                } else score2 = 0;
                if ((locationY > 0) && (locationX > 0) && (event != "") && (score>0)){
                    listNum++;
                    situationList.add(new mSituation("第"+locationY+"列第"+locationX+"排",event,score));
                    adapter.notifyDataSetChanged();
                    locationYText.setText("");
                    locationXText.setText("");
                    eventText.setText("");
                    scoreText.setText("");
                }
                if ((location != "") && (event2 != "") && (score2>0)){
                    listNum++;
                    situationList.add(new mSituation(location+personNumber+"人",event2,score2));
                    adapter.notifyDataSetChanged();
                    locationText.setText("");
                    personNumberText.setText("");
                    event2Text.setText("");
                    score2Text.setText("");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("RecordSituation",MODE_PRIVATE).edit();
        for (int i = 1;i <= listNum;i++){
            editor.putString("location"+number+""+i,situationList.get(i-1).getLocation())
                    .putString("event"+number+""+i,situationList.get(i-1).getEvent())
                    .putInt("score"+number+""+i,situationList.get(i-1).getScore());
        }
        editor.putInt("listNum"+number,listNum).apply();
    }
}
