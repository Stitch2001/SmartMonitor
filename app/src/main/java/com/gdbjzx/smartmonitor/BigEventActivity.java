package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.icu.text.LocaleDisplayNames;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.ContactsContract;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import id.zelory.compressor.Compressor;

public class BigEventActivity extends AppCompatActivity {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int LOGIN = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int RECORD_SITUATION = 3;
    private static final int NOTIFY_CHECKING_SITUATION = 4;
    private static final int RECORD_IMAGE_DATA = 5;
    private static final int SET_REGULATION = 6;

    private static final int PATTERN_NOON = 0;
    private static final int PATTERN_NIGHT = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final short NO_NETWORK = 0;
    private static final short NO_TASK = 1;
    private static final short UNKNOWN_ERROR = 2;

    private ImageView imageView;
    private TextView imageCountText;
    private TextView noTaskText;
    private RelativeLayout layout;

    private int number = 0,max = 0;
    private String[] image = new String[20];
    private short readingSignal = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_event);

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
                    default:
                }
                mDrawerLayout.closeDrawers();
                BigEventActivity.this.finish();
                return true;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*读取和显示图片*/
        imageView = (ImageView) findViewById(R.id.event_image);
        noTaskText = (TextView) findViewById(R.id.no_task_text);
        imageCountText = (TextView) findViewById(R.id.image_count_text);
        layout = (RelativeLayout) findViewById(R.id.layout);
        for (int i = 0;i <= 19;i++) image[i] = "";
        new ReadImages().execute();

        /*设置上一个/下一个按钮*/
        FloatingActionButton nextButton = (FloatingActionButton) findViewById(R.id.next_image);
        FloatingActionButton lastButton = (FloatingActionButton) findViewById(R.id.last_image);
        lastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number > 0){
                    number--;
                    Glide.with(mApplication.getContext()).load(image[number]).into(imageView);
                    imageCountText.setText("图片("+(number+1)+"/"+(max+1)+")");
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number < max){
                    number++;
                    Glide.with(mApplication.getContext()).load(image[number]).into(imageView);
                    imageCountText.setText("图片("+(number+1)+"/"+(max+1)+")");
                }
            }
        });
    }

    class ReadImages extends AsyncTask<Void,Integer,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            AVQuery<AVObject> query = new AVQuery<>("BigEvent");
            query.whereExists("image");
            /*获取所有具有图片的行*/
            List<AVObject> list;
            try {
                list = query.find();
            } catch (AVException e){
                e.printStackTrace();
                if (e.getCode() == 0) readingSignal = NO_NETWORK;
                    else readingSignal = UNKNOWN_ERROR;
                //无网络：0
                //找不到数据：不在此处处理
                //未知错误：2
                return false;
            }
            if (list.size() == 0) {
                readingSignal = NO_TASK;
                return false;
            } else {
                for (int i = 0;i <= list.size()-1;i++) image[i] = list.get(i).getAVFile("image").getUrl();
                max = list.size()-1;
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                /*显示图片*/
                noTaskText.setText("长按读取图片");//防止过多的流量损耗
                layout.setLongClickable(true);
                layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Glide.with(mApplication.getContext()).load(image[number]).into(imageView);//number = 0
                        imageCountText.setVisibility(View.VISIBLE);
                        imageCountText.setText("图片(1/"+(max+1)+")");
                        noTaskText.setVisibility(View.GONE);
                        layout.setLongClickable(false);
                        return false;
                    }
                });
            } else {
                /*获取失败处理*/
                switch (readingSignal){
                    case NO_NETWORK:noTaskText.setText("无网络，请连接网络后重试");break;
                    case NO_TASK:noTaskText.setText("暂时没有大型活动通知");break;
                    case UNKNOWN_ERROR:noTaskText.setText("未知错误");break;
                    default:break;
                }
            }
        }

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
                break;
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
