package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import id.zelory.compressor.Compressor;

public class BigEventActivity extends AppCompatActivity {

    private static final int LOGIN = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private int number = 1;
    private byte[] image[] = new byte[20][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_event);

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
                        intent = new Intent(BigEventActivity.this,LoginActivity.class);
                        startActivityForResult(intent,LOGIN);
                        break;
                    case R.id.nav_monitor:
                        intent = new Intent(BigEventActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        FloatingActionButton nextButton = (FloatingActionButton) findViewById(R.id.next_image);
        FloatingActionButton lastButton = (FloatingActionButton) findViewById(R.id.last_image);

        /*读取图片*/
        AVQuery<AVObject> query = new AVQuery<>("BigEvent");
        query.whereExists("image");

        /*显示图片*/
        ImageView imageView = (ImageView) findViewById(R.id.event_image);

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
        navigationView.setCheckedItem(R.id.nav_big_event);//设置“设置检查顺序”高亮
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
