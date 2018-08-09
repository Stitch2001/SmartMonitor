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
import android.view.DragEvent;
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
    private SharedPreferences.Editor editor;
    private ClassAdapter adapter;

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private int[][] classArray = new int[6][19];

    private Badge[][] badges = new Badge[6][19];

    private int grade,classroom,max,currentNum;//用作循环变量

    private List<mClass> classList = new ArrayList<>();

    private int viewId,lightImageId;

    private boolean isJunior1Loaded,isJunior2Loaded,isJunior3Loaded = false;

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

        /*导入班级布局*/
        initClass();//初始化班级数据
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);//设置线性布局
        recyclerView.setLayoutManager(layoutManager);//指定布局
        adapter = new ClassAdapter(classList);//设置适配器
        recyclerView.setAdapter(adapter);//加载适配器
        initOnClickListener();//初始化点击事件

        /*读取检查顺序*/
        max = 0;
        SharedPreferences pref = getSharedPreferences("RegulationData",MODE_PRIVATE);
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                if (pref.getInt(grade+""+classroom+"",0) != 0){
                    classArray[grade][classroom] = pref.getInt(grade+""+classroom+"",0);
                    if (max < classArray[grade][classroom]) max = classArray[grade][classroom];
                }
            }
        }

        /*写入检查顺序初始化*/
        editor = getSharedPreferences("RegulationData",MODE_PRIVATE).edit();
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

    /*点击事件监听器*/
    private void initOnClickListener(){
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                /*非常愚蠢的枚举法，但没想出其它办法*/
                currentView = layoutManager.findViewByPosition(SENIOR_1);//设置当前年级为高一
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_1);
                        imageView.setImageResource(R.drawable.class_light_1);
                        setOnClickMethod(SENIOR_1,1,R.drawable.class_1);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_2);
                        imageView.setImageResource(R.drawable.class_light_2);
                        setOnClickMethod(SENIOR_1,2,R.drawable.class_2);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_3);
                        imageView.setImageResource(R.drawable.class_light_3);
                        setOnClickMethod(SENIOR_1,3,R.drawable.class_3);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_4);
                        imageView.setImageResource(R.drawable.class_light_4);
                        setOnClickMethod(SENIOR_1,4,R.drawable.class_4);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_5);
                        imageView.setImageResource(R.drawable.class_light_5);
                        setOnClickMethod(SENIOR_1,5,R.drawable.class_5);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_1,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_1,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_1,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_7);
                        imageView.setImageResource(R.drawable.class_light_7);
                        setOnClickMethod(SENIOR_1,7,R.drawable.class_7);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_8);
                        imageView.setImageResource(R.drawable.class_light_8);
                        setOnClickMethod(SENIOR_1,8,R.drawable.class_8);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_9);
                        imageView.setImageResource(R.drawable.class_light_9);
                        setOnClickMethod(SENIOR_1,9,R.drawable.class_9);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_10);
                        imageView.setImageResource(R.drawable.class_light_10);
                        setOnClickMethod(SENIOR_1,10,R.drawable.class_10);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_11);
                        imageView.setImageResource(R.drawable.class_light_11);
                        setOnClickMethod(SENIOR_1,11,R.drawable.class_11);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_12);
                        imageView.setImageResource(R.drawable.class_light_12);
                        setOnClickMethod(SENIOR_1,12,R.drawable.class_12);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_13);
                        imageView.setImageResource(R.drawable.class_light_13);
                        setOnClickMethod(SENIOR_1,13,R.drawable.class_13);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_14);
                        imageView.setImageResource(R.drawable.class_light_14);
                        setOnClickMethod(SENIOR_1,14,R.drawable.class_14);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_15);
                        imageView.setImageResource(R.drawable.class_light_15);
                        setOnClickMethod(SENIOR_1,15,R.drawable.class_15);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_16);
                        imageView.setImageResource(R.drawable.class_light_16);
                        setOnClickMethod(SENIOR_1,16,R.drawable.class_16);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_17);
                        imageView.setImageResource(R.drawable.class_light_17);
                        setOnClickMethod(SENIOR_1,17,R.drawable.class_17);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_1);
                        imageView = (ImageView) currentView.findViewById(R.id.class_18);
                        imageView.setImageResource(R.drawable.class_light_18);
                        setOnClickMethod(SENIOR_1,18,R.drawable.class_18);
                    }
                });

                currentView = layoutManager.findViewByPosition(SENIOR_2);//设置当前年级为高二
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_1);
                        imageView.setImageResource(R.drawable.class_light_1);
                        setOnClickMethod(SENIOR_2,1,R.drawable.class_1);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_2);
                        imageView.setImageResource(R.drawable.class_light_2);
                        setOnClickMethod(SENIOR_2,2,R.drawable.class_2);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_3);
                        imageView.setImageResource(R.drawable.class_light_3);
                        setOnClickMethod(SENIOR_2,3,R.drawable.class_3);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_4);
                        imageView.setImageResource(R.drawable.class_light_4);
                        setOnClickMethod(SENIOR_2,4,R.drawable.class_4);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_5);
                        imageView.setImageResource(R.drawable.class_light_5);
                        setOnClickMethod(SENIOR_2,5,R.drawable.class_5);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_2,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_2,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_2,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_7);
                        imageView.setImageResource(R.drawable.class_light_7);
                        setOnClickMethod(SENIOR_2,7,R.drawable.class_7);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_8);
                        imageView.setImageResource(R.drawable.class_light_8);
                        setOnClickMethod(SENIOR_2,8,R.drawable.class_8);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_9);
                        imageView.setImageResource(R.drawable.class_light_9);
                        setOnClickMethod(SENIOR_2,9,R.drawable.class_9);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_10);
                        imageView.setImageResource(R.drawable.class_light_10);
                        setOnClickMethod(SENIOR_2,10,R.drawable.class_10);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_11);
                        imageView.setImageResource(R.drawable.class_light_11);
                        setOnClickMethod(SENIOR_2,11,R.drawable.class_11);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_12);
                        imageView.setImageResource(R.drawable.class_light_12);
                        setOnClickMethod(SENIOR_2,12,R.drawable.class_12);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_13);
                        imageView.setImageResource(R.drawable.class_light_13);
                        setOnClickMethod(SENIOR_2,13,R.drawable.class_13);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_14);
                        imageView.setImageResource(R.drawable.class_light_14);
                        setOnClickMethod(SENIOR_2,14,R.drawable.class_14);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_15);
                        imageView.setImageResource(R.drawable.class_light_15);
                        setOnClickMethod(SENIOR_2,15,R.drawable.class_15);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_16);
                        imageView.setImageResource(R.drawable.class_light_16);
                        setOnClickMethod(SENIOR_2,16,R.drawable.class_16);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_17);
                        imageView.setImageResource(R.drawable.class_light_17);
                        setOnClickMethod(SENIOR_2,17,R.drawable.class_17);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_18);
                        imageView.setImageResource(R.drawable.class_light_18);
                        setOnClickMethod(SENIOR_2,18,R.drawable.class_18);
                    }
                });

                currentView = layoutManager.findViewByPosition(SENIOR_3);//设置当前年级为高三
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_1);
                        imageView.setImageResource(R.drawable.class_light_1);
                        setOnClickMethod(SENIOR_3,1,R.drawable.class_1);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_2);
                        imageView.setImageResource(R.drawable.class_light_2);
                        setOnClickMethod(SENIOR_3,2,R.drawable.class_2);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_3);
                        imageView.setImageResource(R.drawable.class_light_3);
                        setOnClickMethod(SENIOR_3,3,R.drawable.class_3);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_4);
                        imageView.setImageResource(R.drawable.class_light_4);
                        setOnClickMethod(SENIOR_3,4,R.drawable.class_4);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_5);
                        imageView.setImageResource(R.drawable.class_light_5);
                        setOnClickMethod(SENIOR_3,5,R.drawable.class_5);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_3,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_3,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(SENIOR_3,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_7);
                        imageView.setImageResource(R.drawable.class_light_7);
                        setOnClickMethod(SENIOR_3,7,R.drawable.class_7);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_8);
                        imageView.setImageResource(R.drawable.class_light_8);
                        setOnClickMethod(SENIOR_3,8,R.drawable.class_8);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_9);
                        imageView.setImageResource(R.drawable.class_light_9);
                        setOnClickMethod(SENIOR_3,9,R.drawable.class_9);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_10);
                        imageView.setImageResource(R.drawable.class_light_10);
                        setOnClickMethod(SENIOR_3,10,R.drawable.class_10);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_11);
                        imageView.setImageResource(R.drawable.class_light_11);
                        setOnClickMethod(SENIOR_3,11,R.drawable.class_11);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_12);
                        imageView.setImageResource(R.drawable.class_light_12);
                        setOnClickMethod(SENIOR_3,12,R.drawable.class_12);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_13);
                        imageView.setImageResource(R.drawable.class_light_13);
                        setOnClickMethod(SENIOR_3,13,R.drawable.class_13);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_14);
                        imageView.setImageResource(R.drawable.class_light_14);
                        setOnClickMethod(SENIOR_3,14,R.drawable.class_14);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_15);
                        imageView.setImageResource(R.drawable.class_light_15);
                        setOnClickMethod(SENIOR_3,15,R.drawable.class_15);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_16);
                        imageView.setImageResource(R.drawable.class_light_16);
                        setOnClickMethod(SENIOR_3,16,R.drawable.class_16);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_17);
                        imageView.setImageResource(R.drawable.class_light_17);
                        setOnClickMethod(SENIOR_3,17,R.drawable.class_17);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(SENIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_18);
                        imageView.setImageResource(R.drawable.class_light_18);
                        setOnClickMethod(SENIOR_3,18,R.drawable.class_18);
                    }
                });

/*                recyclerView.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View view, DragEvent dragEvent) {
                        Log.d("Count",adapter.getItemCount()+"");
                        Log.d("isLoaded",isJunior1Loaded+"");
                        if (adapter.getItemCount() == 3)
                            if (!isJunior1Loaded){
                                isJunior1Loaded = true;
                                currentView = layoutManager.findViewByPosition(JUNIOR_1);//设置当前年级为初一
                                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_1);
                                        imageView.setImageResource(R.drawable.class_light_1);
                                        setOnClickMethod(JUNIOR_1,1,R.drawable.class_1);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_2);
                                        imageView.setImageResource(R.drawable.class_light_2);
                                        setOnClickMethod(JUNIOR_1,2,R.drawable.class_2);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_3);
                                        imageView.setImageResource(R.drawable.class_light_3);
                                        setOnClickMethod(JUNIOR_1,3,R.drawable.class_3);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_4);
                                        imageView.setImageResource(R.drawable.class_light_4);
                                        setOnClickMethod(JUNIOR_1,4,R.drawable.class_4);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_5);
                                        imageView.setImageResource(R.drawable.class_light_5);
                                        setOnClickMethod(JUNIOR_1,5,R.drawable.class_5);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                                        imageView.setImageResource(R.drawable.class_light_6);
                                        setOnClickMethod(JUNIOR_1,6,R.drawable.class_6);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                                        imageView.setImageResource(R.drawable.class_light_6);
                                        setOnClickMethod(JUNIOR_1,6,R.drawable.class_6);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                                        imageView.setImageResource(R.drawable.class_light_6);
                                        setOnClickMethod(JUNIOR_1,6,R.drawable.class_6);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_7);
                                        imageView.setImageResource(R.drawable.class_light_7);
                                        setOnClickMethod(JUNIOR_1,7,R.drawable.class_7);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_8);
                                        imageView.setImageResource(R.drawable.class_light_8);
                                        setOnClickMethod(JUNIOR_1,8,R.drawable.class_8);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_9);
                                        imageView.setImageResource(R.drawable.class_light_9);
                                        setOnClickMethod(JUNIOR_1,9,R.drawable.class_9);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_10);
                                        imageView.setImageResource(R.drawable.class_light_10);
                                        setOnClickMethod(JUNIOR_1,10,R.drawable.class_10);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_11);
                                        imageView.setImageResource(R.drawable.class_light_11);
                                        setOnClickMethod(JUNIOR_1,11,R.drawable.class_11);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_12);
                                        imageView.setImageResource(R.drawable.class_light_12);
                                        setOnClickMethod(JUNIOR_1,12,R.drawable.class_12);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_13);
                                        imageView.setImageResource(R.drawable.class_light_13);
                                        setOnClickMethod(JUNIOR_1,13,R.drawable.class_13);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_14);
                                        imageView.setImageResource(R.drawable.class_light_14);
                                        setOnClickMethod(JUNIOR_1,14,R.drawable.class_14);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_15);
                                        imageView.setImageResource(R.drawable.class_light_15);
                                        setOnClickMethod(JUNIOR_1,15,R.drawable.class_15);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_16);
                                        imageView.setImageResource(R.drawable.class_light_16);
                                        setOnClickMethod(JUNIOR_1,16,R.drawable.class_16);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_17);
                                        imageView.setImageResource(R.drawable.class_light_17);
                                        setOnClickMethod(JUNIOR_1,17,R.drawable.class_17);
                                    }
                                });
                                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        currentView = layoutManager.findViewByPosition(JUNIOR_1);
                                        imageView = (ImageView) currentView.findViewById(R.id.class_18);
                                        imageView.setImageResource(R.drawable.class_light_18);
                                        setOnClickMethod(JUNIOR_1,18,R.drawable.class_18);
                                    }
                                });
                            }
                            return true;
                    }
                });

/*                currentView = layoutManager.findViewByPosition(JUNIOR_2);//设置当前年级为初二
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_1);
                        imageView.setImageResource(R.drawable.class_light_1);
                        setOnClickMethod(JUNIOR_2,1,R.drawable.class_1);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_2);
                        imageView.setImageResource(R.drawable.class_light_2);
                        setOnClickMethod(JUNIOR_2,2,R.drawable.class_2);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_3);
                        imageView.setImageResource(R.drawable.class_light_3);
                        setOnClickMethod(JUNIOR_2,3,R.drawable.class_3);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_4);
                        imageView.setImageResource(R.drawable.class_light_4);
                        setOnClickMethod(JUNIOR_2,4,R.drawable.class_4);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_5);
                        imageView.setImageResource(R.drawable.class_light_5);
                        setOnClickMethod(JUNIOR_2,5,R.drawable.class_5);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(JUNIOR_2,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(JUNIOR_2,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(JUNIOR_2,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_7);
                        imageView.setImageResource(R.drawable.class_light_7);
                        setOnClickMethod(JUNIOR_2,7,R.drawable.class_7);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_8);
                        imageView.setImageResource(R.drawable.class_light_8);
                        setOnClickMethod(JUNIOR_2,8,R.drawable.class_8);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_9);
                        imageView.setImageResource(R.drawable.class_light_9);
                        setOnClickMethod(JUNIOR_2,9,R.drawable.class_9);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_10);
                        imageView.setImageResource(R.drawable.class_light_10);
                        setOnClickMethod(JUNIOR_2,10,R.drawable.class_10);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_11);
                        imageView.setImageResource(R.drawable.class_light_11);
                        setOnClickMethod(JUNIOR_2,11,R.drawable.class_11);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_12);
                        imageView.setImageResource(R.drawable.class_light_12);
                        setOnClickMethod(JUNIOR_2,12,R.drawable.class_12);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_13);
                        imageView.setImageResource(R.drawable.class_light_13);
                        setOnClickMethod(JUNIOR_2,13,R.drawable.class_13);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_14);
                        imageView.setImageResource(R.drawable.class_light_14);
                        setOnClickMethod(JUNIOR_2,14,R.drawable.class_14);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_15);
                        imageView.setImageResource(R.drawable.class_light_15);
                        setOnClickMethod(JUNIOR_2,15,R.drawable.class_15);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_16);
                        imageView.setImageResource(R.drawable.class_light_16);
                        setOnClickMethod(JUNIOR_2,16,R.drawable.class_16);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_17);
                        imageView.setImageResource(R.drawable.class_light_17);
                        setOnClickMethod(JUNIOR_2,17,R.drawable.class_17);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_2);
                        imageView = (ImageView) currentView.findViewById(R.id.class_18);
                        imageView.setImageResource(R.drawable.class_light_18);
                        setOnClickMethod(JUNIOR_2,18,R.drawable.class_18);
                    }
                });

                currentView = layoutManager.findViewByPosition(JUNIOR_3);//设置当前年级为初三
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_1);
                        imageView.setImageResource(R.drawable.class_light_1);
                        setOnClickMethod(JUNIOR_3,1,R.drawable.class_1);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_2);
                        imageView.setImageResource(R.drawable.class_light_2);
                        setOnClickMethod(JUNIOR_3,2,R.drawable.class_2);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_3);
                        imageView.setImageResource(R.drawable.class_light_3);
                        setOnClickMethod(JUNIOR_3,3,R.drawable.class_3);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_4);
                        imageView.setImageResource(R.drawable.class_light_4);
                        setOnClickMethod(JUNIOR_3,4,R.drawable.class_4);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_5);
                        imageView.setImageResource(R.drawable.class_light_5);
                        setOnClickMethod(JUNIOR_3,5,R.drawable.class_5);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(JUNIOR_3,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(JUNIOR_3,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_6);
                        imageView.setImageResource(R.drawable.class_light_6);
                        setOnClickMethod(JUNIOR_3,6,R.drawable.class_6);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_7);
                        imageView.setImageResource(R.drawable.class_light_7);
                        setOnClickMethod(JUNIOR_3,7,R.drawable.class_7);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_8);
                        imageView.setImageResource(R.drawable.class_light_8);
                        setOnClickMethod(JUNIOR_3,8,R.drawable.class_8);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_9);
                        imageView.setImageResource(R.drawable.class_light_9);
                        setOnClickMethod(JUNIOR_3,9,R.drawable.class_9);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_10);
                        imageView.setImageResource(R.drawable.class_light_10);
                        setOnClickMethod(JUNIOR_3,10,R.drawable.class_10);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_11);
                        imageView.setImageResource(R.drawable.class_light_11);
                        setOnClickMethod(JUNIOR_3,11,R.drawable.class_11);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_12);
                        imageView.setImageResource(R.drawable.class_light_12);
                        setOnClickMethod(JUNIOR_3,12,R.drawable.class_12);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_13);
                        imageView.setImageResource(R.drawable.class_light_13);
                        setOnClickMethod(JUNIOR_3,13,R.drawable.class_13);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_14);
                        imageView.setImageResource(R.drawable.class_light_14);
                        setOnClickMethod(JUNIOR_3,14,R.drawable.class_14);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_15);
                        imageView.setImageResource(R.drawable.class_light_15);
                        setOnClickMethod(JUNIOR_3,15,R.drawable.class_15);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_16);
                        imageView.setImageResource(R.drawable.class_light_16);
                        setOnClickMethod(JUNIOR_3,16,R.drawable.class_16);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_17);
                        imageView.setImageResource(R.drawable.class_light_17);
                        setOnClickMethod(JUNIOR_3,17,R.drawable.class_17);
                    }
                });
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentView = layoutManager.findViewByPosition(JUNIOR_3);
                        imageView = (ImageView) currentView.findViewById(R.id.class_18);
                        imageView.setImageResource(R.drawable.class_light_18);
                        setOnClickMethod(JUNIOR_3,18,R.drawable.class_18);
                    }
                });*/
                renewButtonImage();
            }
        });
    }

    /*点击事件的具体方法*/
    private void setOnClickMethod(int grade,int classroom,int imageId){
        if (classArray[grade][classroom] == 0){
            max++;classArray[grade][classroom] = max;//将此班级置于队列末尾
            renewButtonImage();
        } else {
            if (badges[grade][classroom] != null){
                badges[grade][classroom].hide(true);
                badges[grade][classroom] = null;
            }//隐藏角标
            imageView.setImageResource(imageId);
            renewList(grade,classroom);
            renewButtonImage();
        }
    }

    /*更新按钮图片和角标*/
    private void renewButtonImage(){
        for (grade = SENIOR_1;grade <= SENIOR_3;grade++) {
            currentView = layoutManager.findViewByPosition(grade);
            for (classroom = 1; classroom <= 18; classroom++) {
                switch (classroom) {
                    case 1:
                        viewId = R.id.class_1;
                        lightImageId = R.drawable.class_light_1;
                        break;
                    case 2:
                        viewId = R.id.class_2;
                        lightImageId = R.drawable.class_light_2;
                        break;
                    case 3:
                        viewId = R.id.class_3;
                        lightImageId = R.drawable.class_light_3;
                        break;
                    case 4:
                        viewId = R.id.class_4;
                        lightImageId = R.drawable.class_light_4;
                        break;
                    case 5:
                        viewId = R.id.class_5;
                        lightImageId = R.drawable.class_light_5;
                        break;
                    case 6:
                        viewId = R.id.class_6;
                        lightImageId = R.drawable.class_light_6;
                        break;
                    case 7:
                        viewId = R.id.class_7;
                        lightImageId = R.drawable.class_light_7;
                        break;
                    case 8:
                        viewId = R.id.class_8;
                        lightImageId = R.drawable.class_light_8;
                        break;
                    case 9:
                        viewId = R.id.class_9;
                        lightImageId = R.drawable.class_light_9;
                        break;
                    case 10:
                        viewId = R.id.class_10;
                        lightImageId = R.drawable.class_light_10;
                        break;
                    case 11:
                        viewId = R.id.class_11;
                        lightImageId = R.drawable.class_light_11;
                        break;
                    case 12:
                        viewId = R.id.class_12;
                        lightImageId = R.drawable.class_light_12;
                        break;
                    case 13:
                        viewId = R.id.class_13;
                        lightImageId = R.drawable.class_light_13;
                        break;
                    case 14:
                        viewId = R.id.class_14;
                        lightImageId = R.drawable.class_light_14;
                        break;
                    case 15:
                        viewId = R.id.class_15;
                        lightImageId = R.drawable.class_light_15;
                        break;
                    case 16:
                        viewId = R.id.class_16;
                        lightImageId = R.drawable.class_light_16;
                        break;
                    case 17:
                        viewId = R.id.class_17;
                        lightImageId = R.drawable.class_light_17;
                        break;
                    case 18:
                        viewId = R.id.class_18;
                        lightImageId = R.drawable.class_light_18;
                        break;
                    default:
                        break;
                }
                if (classArray[grade][classroom] != 0) {
                    imageView = (ImageView) currentView.findViewById(viewId);
                    if (badges[grade][classroom] == null) {
                        imageView.setImageResource(lightImageId);
                        badges[grade][classroom] = new QBadgeView(MyApplication.getContext()).bindTarget(imageView)
                                .setBadgeText(classArray[grade][classroom] + "").setBadgeGravity(Gravity.TOP | Gravity.END)
                                .setGravityOffset(0, 0, true);
                    } else {
                        imageView.setImageResource(lightImageId);
                        badges[grade][classroom].setBadgeText(classArray[grade][classroom] + "");
                    }
                }
            }
        }
    }

    /*对检查顺序表重新排序并实时保存*/
    private void renewList(int currentGrade,int currentClassroom){
        currentNum = classArray[currentGrade][currentClassroom];
        max--;classArray[currentGrade][currentClassroom] = 0;
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                if (classArray[grade][classroom] >= currentNum) classArray[grade][classroom]--;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                editor.putInt(grade+""+classroom+"",classArray[grade][classroom]);
            }
        }
        editor.apply();
    }
}
