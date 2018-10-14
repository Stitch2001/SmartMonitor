package com.gdbjzx.smartmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;

import java.util.Calendar;
import java.util.TimeZone;

import static android.icu.text.DateTimePatternGenerator.DAY;

public class AlarmSettingsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_alarm_settings);

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
                    case R.id.nav_about:
                        intent = new Intent(mApplication.getContext(),AboutActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*设置提交按钮*/
        FloatingActionButton submit = (FloatingActionButton) findViewById(R.id.submit);
        final Spinner day1 = (Spinner) findViewById(R.id.day1);
        final Spinner day2 = (Spinner) findViewById(R.id.day2);
        final Spinner day3 = (Spinner) findViewById(R.id.day3);
        final Spinner day4 = (Spinner) findViewById(R.id.day4);
        final Spinner grade1 = (Spinner) findViewById(R.id.grade1);
        final Spinner grade2 = (Spinner) findViewById(R.id.grade2);
        final Spinner grade3 = (Spinner) findViewById(R.id.grade3);
        final Spinner grade4 = (Spinner) findViewById(R.id.grade4);
        final Spinner time1 = (Spinner) findViewById(R.id.time1);
        final Spinner time2 = (Spinner) findViewById(R.id.time2);
        final Spinner time3 = (Spinner) findViewById(R.id.time3);
        final Spinner time4 = (Spinner) findViewById(R.id.time4);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!day1.getSelectedItem().toString().equals("星期") && !grade1.getSelectedItem().toString().equals("年级")
                        && !time1.getSelectedItem().toString().equals("时间")){
                    int mDay = 0;
                    switch (day1.getSelectedItem().toString()){
                        case "周一":mDay = Calendar.MONDAY;break;
                        case "周二":mDay = Calendar.TUESDAY;break;
                        case "周三":mDay = Calendar.WEDNESDAY;break;
                        case "周四":mDay = Calendar.THURSDAY;break;
                        case "周五":mDay = Calendar.FRIDAY;break;
                        default:break;
                    }
                    setTime(mDay,6,30);
                    if (time1.getSelectedItem().toString().equals("午休")) setTime(mDay,12,0);
                    else if (time1.getSelectedItem().toString().equals("晚修")) setTime(mDay,17,40);
                    //此处设置通知远远不够，因为要获取自启动权限，而本软件没有必要这样做。后期考虑通过微信端提醒
                    Toast.makeText(AlarmSettingsActivity.this,"设置通知成功! ", Toast.LENGTH_LONG).show();
                }
                if (!day2.getSelectedItem().toString().equals("星期") && !grade2.getSelectedItem().toString().equals("年级")
                        && !time2.getSelectedItem().toString().equals("时间")){
                    int mDay = 0;
                    switch (day1.getSelectedItem().toString()){
                        case "周一":mDay = Calendar.MONDAY;break;
                        case "周二":mDay = Calendar.TUESDAY;break;
                        case "周三":mDay = Calendar.WEDNESDAY;break;
                        case "周四":mDay = Calendar.THURSDAY;break;
                        case "周五":mDay = Calendar.FRIDAY;break;
                        default:break;
                    }
                    setTime(mDay,6,30);
                    if (time1.getSelectedItem().toString().equals("午休")) setTime(mDay,12,0);
                    else if (time1.getSelectedItem().toString().equals("晚修")) setTime(mDay,17,40);
                    //此处设置通知远远不够，因为要获取自启动权限，而本软件没有必要这样做。后期考虑通过微信端提醒
                }
                if (!day3.getSelectedItem().toString().equals("星期") && !grade3.getSelectedItem().toString().equals("年级")
                        && !time3.getSelectedItem().toString().equals("时间")){
                    int mDay = 0;
                    switch (day1.getSelectedItem().toString()){
                        case "周一":mDay = Calendar.MONDAY;break;
                        case "周二":mDay = Calendar.TUESDAY;break;
                        case "周三":mDay = Calendar.WEDNESDAY;break;
                        case "周四":mDay = Calendar.THURSDAY;break;
                        case "周五":mDay = Calendar.FRIDAY;break;
                        default:break;
                    }
                    setTime(mDay,6,30);
                    if (time1.getSelectedItem().toString().equals("午休")) setTime(mDay,12,0);
                    else if (time1.getSelectedItem().toString().equals("晚修")) setTime(mDay,17,40);
                    //此处设置通知远远不够，因为要获取自启动权限，而本软件没有必要这样做。后期考虑通过微信端提醒
                }
                if (!day4.getSelectedItem().toString().equals("星期") && !grade4.getSelectedItem().toString().equals("年级")
                        && !time4.getSelectedItem().toString().equals("时间")){
                    int mDay = 0;
                    switch (day1.getSelectedItem().toString()){
                        case "周一":mDay = Calendar.MONDAY;break;
                        case "周二":mDay = Calendar.TUESDAY;break;
                        case "周三":mDay = Calendar.WEDNESDAY;break;
                        case "周四":mDay = Calendar.THURSDAY;break;
                        case "周五":mDay = Calendar.FRIDAY;break;
                        default:break;
                    }
                    setTime(mDay,6,30);
                    if (time1.getSelectedItem().toString().equals("午休")) setTime(mDay,12,0);
                    else if (time1.getSelectedItem().toString().equals("晚修")) setTime(mDay,17,40);
                    //此处设置通知远远不够，因为要获取自启动权限，而本软件没有必要这样做。后期考虑通过微信端提醒
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_set_alarm);//设置“通知设置”高亮

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

    private void setTime(int mDay,int mHour,int mMinute){
        Intent intent = new Intent(AlarmSettingsActivity.this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(AlarmSettingsActivity.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();// 开机之后到现在的运行时间(包括睡眠时间)
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        //calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.DAY_OF_WEEK,mDay);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_WEEK, 7);
            selectTime = calendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        firstTime += time;
        // 进行闹铃注册
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, firstTime, 7*24*3600, sender);
    }
}
