package com.gdbjzx.smartmonitor;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NotifyCheckingSituationActivity extends AppCompatActivity {

    private List<mClass> classList = new ArrayList<>();

    private static final int NON_GRADE = -1;
    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private static final int PATTERN_NOON = 0;
    private static final int PATTERN_NIGHT = 1;

    public int leftClass = 0;//还未检查的班级数
    private List<mGradeAndClass> regulationList = new ArrayList<>();
    private int max;
    private int[] classArray = new int[19];

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

        /*判断是检查午休还是晚修*/
        SharedPreferences pref;
        int pattern = getIntent().getIntExtra("pattern",-1);
        if (pattern == PATTERN_NOON) pref = getSharedPreferences("RegulationNoonData",MODE_PRIVATE);
        else if (pattern == PATTERN_NIGHT) pref = getSharedPreferences("RegulationNightData",MODE_PRIVATE);
        else pref = getSharedPreferences("null",MODE_PRIVATE);

        /*读取该年级中的检查顺序并重新排列*/
        for (int i = 0;i <= 54;i++) regulationList.add(new mGradeAndClass(-1,0, 0));
        max = 0;
        for (int grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (int classroom = 1;classroom <= 18;classroom++){
                int array = pref.getInt(grade+""+classroom+"",0);
                if (array != 0){
                    regulationList.get(array).setGrade(grade);
                    regulationList.get(array).setClassroom(classroom);
                    regulationList.get(array).setArray(array);
                    if (max < array) max = array;
                }
            }
        }

        initClassItem();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//设置线性布局
        recyclerView.setLayoutManager(layoutManager);//指定布局
        NotifyCheckingSituationAdapter adapter = new NotifyCheckingSituationAdapter(classList,this);//设置适配器
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
        boolean[] classroomBool = {false,false,false,false,false,false,false,false,false,false,
                false,false,false,false,false,false,false,false,false};
        int lastGrade = NON_GRADE;
        int currentGrade = NON_GRADE;

        for (int i = 1;i <= max;i++){
            currentGrade = regulationList.get(i).getGrade();
            if (currentGrade == lastGrade){
                classroomBool[regulationList.get(i).getClassroom()] = true;
                classArray[regulationList.get(i).getClassroom()] = regulationList.get(i).getArray();
            } else {
                if (lastGrade != NON_GRADE){
                    classList.add(new mClass(lastGrade,classroomBool,i,classArray,null));
                }
                lastGrade = currentGrade;
                classroomBool = new boolean[19];
                for (int j = 1;j <= 18;j++) classroomBool[j] = false;//初始化classroomBool[]
                classArray = new int[19];
                for (int j = 1;j <= 18;j++) classArray[j] = 0;//初始化classArray[]
                classroomBool[regulationList.get(i).getClassroom()] = true;
                classArray[regulationList.get(i).getClassroom()] = regulationList.get(i).getArray();
            }
        }
        if (lastGrade != NON_GRADE) classList.add(new mClass(lastGrade,classroomBool,max,classArray,null));
    }

}
