package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class SetRegulationActivity extends AppCompatActivity  {

    private static final int LOGIN = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private LinearLayoutManager layoutManager;
    private View currentView;

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private int[][] classArray = new int[6][19];

    private int grade,classroom,max,currentNum;//用作循环变量

    private List<mClass> classList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_regulation);

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

        /*读取检查顺序*/
        max = 0;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                if (pref.getInt(grade+""+classroom+"",0) != 0){
                    classArray[grade][classroom] = pref.getInt(grade+""+classroom+"",0);
                    if (max < classArray[grade][classroom]) max = classArray[grade][classroom];
                }
            }
        }

        /*导入班级布局*/
        initClass();//初始化班级数据
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);//设置线性布局
        recyclerView.setLayoutManager(layoutManager);//指定布局
        ClassAdapter adapter = new ClassAdapter(classList);//设置适配器
        recyclerView.setAdapter(adapter);//加载适配器
        initOnClickListener();//初始化点击事件
    }

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
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(R.id.nav_set_regulation);//设置“设置检查顺序”高亮
    }

    private void initClass(){
        mClass grade1 = new mClass("初一");
        mClass grade2 = new mClass("初二");
        mClass grade3 = new mClass("初三");
        mClass grade4 = new mClass("高一");
        mClass grade5 = new mClass("高二");
        mClass grade6 = new mClass("高三");
        classList.add(grade4);//考虑到本校实际情况，从高一开始导入布局
        classList.add(grade5);
        classList.add(grade6);
        classList.add(grade1);
        classList.add(grade2);
        classList.add(grade3);
    }

    private void initOnClickListener(){
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                /*非常愚蠢的枚举法，但没想出其它办法*/
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (classArray[SENIOR_1][1] == 0){
                            max++;classArray[SENIOR_1][1] = max;//将此班级置于队列末尾
                            renewButtonImage();
                        } else {
                            //隐藏角标
                            new QBadgeView(MyApplication.getContext()).bindTarget(imageView).hide(true);
                            renewList(SENIOR_1,1);
                            renewButtonImage();
                        }
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (classArray[SENIOR_1][2] == 0){
                            max++;classArray[SENIOR_1][2] = max;//将此班级置于队列末尾
                            renewButtonImage();
                        } else {
                            //隐藏角标
                            new QBadgeView(MyApplication.getContext()).bindTarget(imageView).hide(true);
                            renewList(SENIOR_1,2);
                            renewButtonImage();
                        }
                    }
                });
            }
        });
    }

    private void renewButtonImage(){
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                if (classArray[grade][classroom] != 0) {
                    switch (grade){
                        case SENIOR_1:
                            currentView = layoutManager.findViewByPosition(SENIOR_1);
                            switch (classroom){
                                case 1:
                                    Log.d("Set","OK");
                                    imageView = (ImageView) currentView.findViewById(R.id.class_1);
                                    new QBadgeView(MyApplication.getContext()).bindTarget(imageView).setBadgeText(classArray[grade][classroom]+"")
                                        .setBadgeGravity(Gravity.TOP|Gravity.END).setGravityOffset(0,0,true);
                                    break;
                                case 2:
                                    imageView = (ImageView) currentView.findViewById(R.id.class_2);
                                    new QBadgeView(MyApplication.getContext()).bindTarget(imageView).setBadgeText(classArray[grade][classroom]+"")
                                            .setBadgeGravity(Gravity.TOP|Gravity.END).setGravityOffset(0,0,true);
                                    break;
                                default:
                            }
                            break;
                        default:
                    }
                };
            }
        }
    }

    /*对检查顺序表重新排序*/
    private void renewList(int currentGrade,int currentClassroom){
        currentNum = classArray[currentGrade][currentClassroom];
        max--;classArray[currentGrade][currentClassroom] = 0;
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                if (classArray[grade][classroom] == currentNum) classArray[grade][classroom]--;
            }
        }
    }

}
