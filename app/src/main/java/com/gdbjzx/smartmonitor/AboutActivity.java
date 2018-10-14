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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;

public class AboutActivity extends AppCompatActivity {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final int LOGIN = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int RECORD_SITUATION = 3;
    private static final int NOTIFY_CHECKING_SITUATION = 4;
    private static final int RECORD_IMAGE_DATA = 5;
    private static final int SET_REGULATION = 6;

    private static final int PATTERN_NOON = 0;
    private static final int PATTERN_NIGHT = 1;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ////////////////////////////////////////////////////////////////////////////////////////////
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        /*设置Toolbar为默认ActionBar，设置图标*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }

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
                        AVUser.logOut();
                        intent = new Intent(mApplication.getContext(),LoginActivity.class);
                        startActivityForResult(intent,LOGIN);
                        break;
                    case R.id.nav_set_noon_regulation:
                        intent = new Intent(mApplication.getContext(),SetRegulationActivity.class);
                        intent.putExtra("pattern",PATTERN_NOON);
                        startActivityForResult(intent,SET_REGULATION);
                        break;
                    case R.id.nav_set_night_regulation:
                        intent = new Intent(mApplication.getContext(),SetRegulationActivity.class);
                        intent.putExtra("pattern",PATTERN_NIGHT);
                        startActivityForResult(intent,SET_REGULATION);
                        break;
                    case R.id.nav_big_event:
                        intent = new Intent(mApplication.getContext(),BigEventActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_set_alarm:
                        intent = new Intent(mApplication.getContext(),AlarmSettingsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_about);//设置“关于”高亮

        /*把当前用户名显示在NavigationView上*/
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                TextView userNameText = findViewById(R.id.username_text);
                if (AVUser.getCurrentUser() != null) userNameText.setText("欢迎你，"+AVUser.getCurrentUser().getUsername());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    /*点击按钮出现左侧菜单*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home://打开侧边栏
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
