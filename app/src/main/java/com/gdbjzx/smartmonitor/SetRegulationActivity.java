package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SetRegulationActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private NavigationView navigationView;

    private static final int LOGIN = 1;

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

        /*导入班级布局*/
        initClass();//初始化班级数据
        RecyclerView listItem = (RecyclerView) findViewById(R.id.list_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//设置线性布局
        listItem.setLayoutManager(layoutManager);//指定布局
        ClassAdapter adapter = new ClassAdapter(classList);//设置适配器
        listItem.setAdapter(adapter);//加载适配器
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
        int gradeArray1[] = {101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118};
        int gradeArray2[] = {201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218};
        int gradeArray3[] = {301,302,303,304,305,306,307,308,309,310,311,312,313,314,315,316,317,318};
        int gradeArray4[] = {401,402,403,404,405,406,407,408,409,410,411,412,413,414,415,416,417,418};
        int gradeArray5[] = {501,502,503,504,505,506,507,508,509,510,511,512,513,514,515,516,517,518};
        int gradeArray6[] = {601,602,603,604,605,606,607,608,609,610,611,612,613,614,615,616,617,618};
        mClass grade1 = new mClass("初一",gradeArray1);
        mClass grade2 = new mClass("初二",gradeArray2);
        mClass grade3 = new mClass("初三",gradeArray3);
        mClass grade4 = new mClass("高一",gradeArray4);
        mClass grade5 = new mClass("高二",gradeArray5);
        mClass grade6 = new mClass("高三",gradeArray6);
        classList.add(grade4);//考虑到本校实际情况，从高一开始导入布局
        classList.add(grade5);
        classList.add(grade6);
        classList.add(grade1);
        classList.add(grade2);
        classList.add(grade3);
    }
}
