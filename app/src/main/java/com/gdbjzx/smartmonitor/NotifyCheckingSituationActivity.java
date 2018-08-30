package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NotifyCheckingSituationActivity extends AppCompatActivity {

    private List<mClass> classList = new ArrayList<>();

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private int[][] classArray = new int[6][19];

    public int leftClass = 0;//还未检查的班级数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_checking_situation);
        final ImageView submitData = (ImageView) findViewById(R.id.submit_data);

        ////////////////////////////////////////////////////////////////////////////////////////////
        /*设置Toolbar为默认ActionBar，设置图标*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*读取检查顺序，显示在界面上*/
        SharedPreferences pref = getSharedPreferences("RegulationData",MODE_PRIVATE);
        for (int grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (int classroom = 1;classroom <= 18;classroom++){
                if (pref.getInt(grade+""+classroom+"",0) != 0){
                    classArray[grade][classroom] = pref.getInt(grade+""+classroom+"",0);
                }
            }
        }
        initClassItem();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//设置线性布局
        recyclerView.setLayoutManager(layoutManager);//指定布局
        CheckingClassAdapter adapter = new CheckingClassAdapter(classList,this);//设置适配器
        recyclerView.setAdapter(adapter);//加载适配器

        /*提示还有多少个班级未检查完*/
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (leftClass >0){
                    Toast.makeText(NotifyCheckingSituationActivity.this,"您还有"+leftClass+"个班级未检查",Toast.LENGTH_SHORT)
                            .show();
                    submitData.setClickable(false);
                    submitData.setImageResource(R.color.avoscloud_feedback_text_gray);
                }
            }
        });

        /*完成检查并提交按钮*/
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*开始录入应到实到数据*/
                setResult(RESULT_OK);
                finish();
            }
        });
        setResult(RESULT_FIRST_USER);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    /*点击按钮返回菜单*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    private void initClassItem(){
        classList.add(new mClass("高一",classArray[SENIOR_1],0));
        classList.add(new mClass("高二",classArray[SENIOR_2],0));
        classList.add(new mClass("高三",classArray[SENIOR_3],0));
        classList.add(new mClass("初一",classArray[JUNIOR_1],0));
        classList.add(new mClass("初二",classArray[JUNIOR_2],0));
        classList.add(new mClass("初三",classArray[JUNIOR_3],0));
    }

}
