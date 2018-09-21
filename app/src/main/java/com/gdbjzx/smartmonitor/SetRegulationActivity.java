package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import q.rorbin.badgeview.Badge;

public class SetRegulationActivity extends AppCompatActivity  {

    private static final int LOGIN = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private LinearLayoutManager layoutManager;
    private View currentView;
    private SharedPreferences.Editor editor;
    private SetRegulationAdapter adapter;

    private static final int NON_GRADE = -1;
    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    public int max,gradeMax;
    public List<mGradeAndClass> regulationList = new ArrayList<>();
    private int[] classArray = new int[19];
    private Badge[][] badges = new Badge[6][19];
    private int grade,classroom,currentNum;//用作循环变量
    private List<mClass> classList = new ArrayList<>();
    private int viewId,lightImageId,lastGrade;
    private mClass aClass;
    private Set<String> classroomSet = new HashSet<>();
    private Boolean isFirstUsed = true;
    private boolean[] classroomBool = {false,false,false,false,false,false,false,false,false,false,
            false,false,false,false,false,false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_regulation);
        setResult(RESULT_OK);

        ////////////////////////////////////////////////////////////////////////////////////////////
        /*设置顶部栏*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }//设置Toolbar为默认ActionBar，设置图标

        /*设置导航栏*/
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.nav_logout:
                        /*阻止自动登录*/
                        SharedPreferences.Editor editor = getSharedPreferences("pw",MODE_PRIVATE).edit();//创建存储文件
                        editor.putString("password","");
                        editor.putBoolean("isAuto",false);
                        editor.apply();
                        /*阻止自动登录*/
                        intent = new Intent(SetRegulationActivity.this,LoginActivity.class);
                        startActivityForResult(intent,LOGIN);
                        break;
                    case R.id.nav_monitor:
                        intent = new Intent(SetRegulationActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*读取该年级中的检查顺序并重新排列*/
        for (int i = 0;i <= 54;i++) regulationList.add(new mGradeAndClass(-1,0, 0));
        max = 0;
        SharedPreferences pref = getSharedPreferences("RegulationData",MODE_PRIVATE);
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                int array = pref.getInt(grade+""+classroom+"",0);
                if (array != 0){
                    regulationList.get(array).setGrade(grade);
                    regulationList.get(array).setClassroom(classroom);
                    regulationList.get(array).setArray(array);
                    if (max < array) max = array;
                    classroomSet.add(grade+""+classroom);
                }
            }
        }

        /*设置按钮点击事件*/
        FloatingActionButton submit = (FloatingActionButton) findViewById(R.id.submit);
        final Spinner gradeSpinner = (Spinner) findViewById(R.id.grade_spinner);
        final Spinner classSpinner = (Spinner) findViewById(R.id.classroom_spinner);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gradeString = gradeSpinner.getSelectedItem().toString();
                switch (gradeString){
                    case "高一":grade = SENIOR_1;break;
                    case "高二":grade = SENIOR_2;break;
                    case "高三":grade = SENIOR_3;break;
                    case "初一":grade = JUNIOR_1;break;
                    case "初二":grade = JUNIOR_2;break;
                    case "初三":grade = JUNIOR_3;break;
                }
                classroom = Integer.parseInt(classSpinner.getSelectedItem().toString());
                if (!classroomSet.contains(grade+""+classroom)){
                    classroomSet.add(grade+""+classroom);
                    if (grade == lastGrade){
                        max++;gradeMax++;
                        classList.get(classList.size()-1).setClassroomBool(classroom,true);
                        classList.get(classList.size()-1).setArray(classroom,max);
                        classList.get(classList.size()-1).setMax(gradeMax);
                        regulationList.get(max).setGrade(grade);
                        regulationList.get(max).setClassroom(classroom);
                        regulationList.get(max).setArray(max);
                    } else {
                        classroomBool = new boolean[19];
                        for (int j = 0;j <= 18;j++) classroomBool[j] = false;//初始化classroomBool[]
                        classroomBool[classroom] = true;
                        classArray = new int[19];
                        for (int j = 1;j <= 18;j++) classArray[j] = 0;//初始化classArray[]
                        max++;gradeMax = 1;
                        classArray[classroom] = max;
                        classList.add(new mClass(grade,classroomBool,gradeMax,classArray));
                        regulationList.get(max).setGrade(grade);
                        regulationList.get(max).setClassroom(classroom);
                        regulationList.get(max).setArray(max);
                        lastGrade = grade;
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classroomSet.clear();
                for (int i = 0;i <= classList.size()-1;i++) classList.get(i).deleteAllBadge();
                classList.clear();
                regulationList.clear();
                for (int i = 0;i <= 54;i++) regulationList.add(new mGradeAndClass(-1,0, 0));
                max = 0;
                classroomBool = new boolean[19];
                for (int j = 1;j <= 18;j++) classroomBool[j] = false;//初始化classroomBool[]
                classArray = new int[19];
                for (int j = 1;j <= 18;j++) classArray[j] = 0;//初始化classArray[]
                lastGrade = NON_GRADE;
                adapter.notifyDataSetChanged();
                editor = getSharedPreferences("RegulationData",MODE_PRIVATE).edit();
                editor.clear().apply();
            }
        });
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.back);

        /*导入班级布局*/
        initClass();//初始化班级数据
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);//设置线性布局
        recyclerView.setLayoutManager(layoutManager);//指定布局
        adapter = new SetRegulationAdapter(classList,this);//设置适配器
        recyclerView.setAdapter(adapter);//加载适配器

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /*点击按钮出现左侧菜单*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_set_regulation);//设置“设置检查顺序”高亮
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    private void initClass(){
        gradeMax = 0;
        lastGrade = NON_GRADE;
        int currentGrade = NON_GRADE;
        for (int i = 1;i <= max;i++){
            currentGrade = regulationList.get(i).getGrade();
            if (currentGrade == lastGrade){
                gradeMax++;
                classroomBool[regulationList.get(i).getClassroom()] = true;
                classArray[regulationList.get(i).getClassroom()] = regulationList.get(i).getArray();
            } else {
                if (lastGrade != NON_GRADE){
                    classList.add(new mClass(lastGrade,classroomBool,gradeMax,classArray));
                }
                lastGrade = currentGrade;
                classroomBool = new boolean[19];
                for (int j = 1;j <= 18;j++) classroomBool[j] = false;//初始化classroomBool[]
                classArray = new int[19];
                for (int j = 1;j <= 18;j++) classArray[j] = 0;//初始化classArray[]
                gradeMax = 1;
                classroomBool[regulationList.get(i).getClassroom()] = true;
                classArray[regulationList.get(i).getClassroom()] = regulationList.get(i).getArray();
            }
        }
        if (lastGrade != NON_GRADE) classList.add(new mClass(lastGrade,classroomBool,max,classArray));
        lastGrade = currentGrade;
    }

    /*保存检查顺序*/
    @Override
    protected void onPause() {
        super.onPause();
        editor = getSharedPreferences("RegulationData",MODE_PRIVATE).edit();
        for (int i = 1;i <= max;i++){
            grade = regulationList.get(i).getGrade();
            classroom = regulationList.get(i).getClassroom();
            editor.putInt(grade+""+classroom+"",i);
        }
        editor.apply();
    }
}
