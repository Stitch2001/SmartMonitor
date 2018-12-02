package com.gdbjzx.smartmonitor;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private static final int CAMERA = 0;
    private static final int DELETE = 1;

    private static final int LOADING = 0;
    private static final int RENEW_VIEW = 1;

    private  enum ShowMode {SHOW_IMAGE,SHOW_BLANK};

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private ImageView photoImage;
    private TextView classNameView;
    private FloatingActionButton takePhotoButton;
    private Uri imageUri;
    private FloatingActionButton lastClassButton;
    private FloatingActionButton nextClassButton;

    private int grade = 1,classroom = 1,currentGrade = 1,currentRoom = 1,number = 1,max = 1,takePhotoButtonMode = 1,pattern = 1;
    private int[] classArrayGrade = new int[55];
    private int[] classArrayRoom = new int[55];
    private File classImage;
    private SharedPreferences pref;

    private Handler handler = new Handler(){

        public void handleMessage(Message msg){
            switch (msg.what){
                case RENEW_VIEW:
                    currentGrade = classArrayGrade[number];
                    currentRoom = classArrayRoom[number];
                    switch (currentGrade){
                        case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
                        case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
                        case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
                        case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
                        case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
                        case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
                    }
                    if (number == 1) lastClassButton.setClickable(false);
                    if (number == max) nextClassButton.setImageResource(R.drawable.tick);
                    classImage = new File(getImagePath(currentGrade,currentRoom));
                    if (classImage.exists()){
                        showImage(ShowMode.SHOW_IMAGE);
                        takePhotoButtonMode = DELETE;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    }
                    Toast.makeText(MainActivity.this,"数据读取完成",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoImage = (ImageView) findViewById(R.id.photo_image);
        classNameView = (TextView) findViewById(R.id.class_name);
        /*权限处理*/
        verifyStoragePermissions(MainActivity.this);

        /*判断应该检查午休还是晚修*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());//获取当前时间
        final short formatedDate = Short.parseShort(simpleDateFormat.format(date).toString());
        if ((formatedDate >= 0) && (formatedDate <= 15)) {
            pattern = PATTERN_NOON;
            pref = getSharedPreferences("RegulationNoonData",MODE_PRIVATE);
        } else {
            pattern = PATTERN_NIGHT;
            pref = getSharedPreferences("RegulationNightData",MODE_PRIVATE);
        }

        /*读取班级顺序并储存*/
        max = 0;
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                number = pref.getInt(grade+""+classroom+"",0);
                if (number != 0){
                    classArrayGrade[number] = grade;
                    classArrayRoom[number] = classroom;
                    if (max<number) max = number;
                }
            }
        }

        /*检查更新*/
        checkNewVersion();

        /*非正常关闭恢复*/
        final Boolean isError = pref.getBoolean("isError",false);
        if (isError){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("非正常关闭提醒")
                    .setMessage("您上次的检查数据没有正常提交，是否继续检查？")
                    .setCancelable(true);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /*提醒用户正在读取数据*/
                            number = pref.getInt("CurrentNumber",1);
                            Log.d("GetCurrentNumber",number+"");
                            /*发送消息，更新UI*/
                            Message message = new Message();
                            message.what = RENEW_VIEW;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    /*清除内存中图片*/
                    for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
                        for (classroom = 1;classroom <= 18;classroom++){
                            classImage = new File(getImagePath(grade,classroom));
                            if (classImage.exists()) classImage.delete();
                        }
                    }
                    /*清楚内存中的扣分情况数据*/
                    SharedPreferences.Editor editor = getSharedPreferences("RecordSituation",MODE_PRIVATE).edit();
                    editor.clear().apply();
                    /*清除非正常关闭标记*/
                    editor = pref.edit();
                    editor.putBoolean("isError",false).apply();
                }
            });
            dialog.show();
        }

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
                    case R.id.nav_about:
                        intent = new Intent(mApplication.getContext(),AboutActivity.class);
                        startActivity(intent);
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*读取第一个班级并显示*/
        number = 1;
        currentGrade = classArrayGrade[number];
        currentRoom = classArrayRoom[number];
        switch (currentGrade){
            case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
            case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
            case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
            case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
            case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
            case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
        }

        /*设置拍照按钮*/
        takePhotoButtonMode = CAMERA;
        takePhotoButton = (FloatingActionButton) findViewById(R.id.take_photo);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (takePhotoButtonMode == CAMERA){
                    String path = getImagePath(currentGrade,currentRoom);
                    classImage = new File(path);
                    try {
                        if (classImage.exists()){
                            classImage.delete();
                        }
                        classImage.createNewFile();
                    } catch (IOException e){
                        e.printStackTrace();
                    }//检测是否存在图片，存在则删除
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){//适配Android7.0+版本
                        imageUri = FileProvider.getUriForFile(MainActivity.this,
                                "com.gdbjzx.smartmonitor.fileprovider",classImage);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {//适配Android6.0+版本
                        imageUri = Uri.fromFile(classImage);
                    }//获取图片的本地真实路径
                    /*启动相机*/
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);
                } else if (takePhotoButtonMode == DELETE){
                    /*清除照片*/
                    new File(getImagePath(currentGrade,currentRoom)).delete();
                    showImage(ShowMode.SHOW_BLANK);
                    /*更改按钮*/
                    takePhotoButtonMode = CAMERA;
                    takePhotoButton.setImageResource(android.R.drawable.ic_menu_camera);
                }
            }
        });

        /*设置扣分情况记录按钮*/
        Button recordButton = (Button) findViewById(R.id.record_situations);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mApplication.getContext(),RecordSituationActivity.class);
                intent.putExtra("number",number);
                intent.putExtra("currentGrade",currentGrade);
                intent.putExtra("currentRoom",currentRoom);
                startActivityForResult(intent,RECORD_SITUATION);
            }
        });

        /*设置上一个/下一个班级按钮*/
        lastClassButton = (FloatingActionButton) findViewById(R.id.last_class);
        nextClassButton = (FloatingActionButton) findViewById(R.id.next_class);
        lastClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextClassButton.setImageResource(android.R.drawable.ic_media_ff);
                if (number > 1){//判断前面是否有班级
                    /*更新文字*/
                    number--;
                    currentGrade = classArrayGrade[number];
                    currentRoom = classArrayRoom[number];
                    switch (currentGrade){
                        case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
                        case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
                        case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
                        case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
                        case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
                        case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
                    }
                    /*更新图片和按钮*/
                    classImage = new File(getImagePath(currentGrade,currentRoom));
                    if (classImage.exists()) {
                        showImage(ShowMode.SHOW_IMAGE);
                        takePhotoButtonMode = DELETE;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    } else {
                        showImage(ShowMode.SHOW_BLANK);
                        takePhotoButtonMode = CAMERA;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                    if (number == 1){
                        /*设置按钮不可用*/
                        lastClassButton.setClickable(false);
                    }
                }
            }
        });
        nextClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*设置“上一个”按钮可用*/
                lastClassButton.setClickable(true);
                if (number < max){//判断后面是否有班级
                    /*更新文字*/
                    number++;
                    currentGrade = classArrayGrade[number];
                    currentRoom = classArrayRoom[number];
                    switch (currentGrade){
                        case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
                        case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
                        case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
                        case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
                        case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
                        case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
                    }
                    /*更新图片和按钮*/
                    classImage = new File(getImagePath(currentGrade,currentRoom));
                    if (classImage.exists()) {
                        showImage(ShowMode.SHOW_IMAGE);
                        takePhotoButtonMode = DELETE;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    } else {
                        showImage(ShowMode.SHOW_BLANK);
                        takePhotoButtonMode = CAMERA;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                    if (number == max){
                        /*将按钮设置为提交按钮*/
                        nextClassButton.setImageResource(R.drawable.tick);
                    }
                } else {
                    /*检查完成*/
                    Intent intent = new Intent(MainActivity.this,NotifyCheckingSituationActivity.class);
                    intent.putExtra("pattern",pattern);
                    startActivityForResult(intent,NOTIFY_CHECKING_SITUATION);
                }
            }
        });

        /*强制登录*/
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivityForResult(intent,LOGIN);
        } else if (max == 0) {
            /*强制设置检查顺序*/
            Toast.makeText(MainActivity.this,"请先设置检查顺序",Toast.LENGTH_SHORT);
            Intent intent = new Intent(mApplication.getContext(),SetRegulationActivity.class);
            intent.putExtra("pattern",pattern);
            startActivityForResult(intent,SET_REGULATION);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_monitor);//设置“检查”高亮

        /*把当前用户名显示在NavigationView上*/
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                TextView userNameText = findViewById(R.id.username_text);
                if ((AVUser.getCurrentUser() != null) && (max != 0)) userNameText.setText("欢迎你，"+AVUser.getCurrentUser().getUsername());
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
            case R.id.switch_class://切换班级
                Intent intent = new Intent(MainActivity.this,NotifyCheckingSituationActivity.class);
                intent.putExtra("pattern",pattern);
                startActivityForResult(intent,NOTIFY_CHECKING_SITUATION);
                break;
            default:
                break;
        }
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case LOGIN:
                if (resultCode == RESULT_CANCELED) finish();
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        classImage = new File(String.valueOf(classImage));
                    } else {
                        classImage = new File(imageUri.getEncodedPath());
                    }
                    /*显示拍摄的照片*/
                    showImage(ShowMode.SHOW_IMAGE);
                    /*更新按钮*/
                    takePhotoButtonMode = DELETE;
                    takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    /*实时保存状态*/
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isError",true).putInt("CurrentNumber",number).apply();
                    Log.d("CurrentNumber",number+"");
                }
                break;
            case RECORD_SITUATION:
                break;
            case NOTIFY_CHECKING_SITUATION:
                if (resultCode == RESULT_OK){
                    /*开始录入应到实到数据*/
                    Intent intent = new Intent(MainActivity.this,RecordImageDataActivity.class);
                    intent.putExtra("pattern",pattern);
                    startActivityForResult(intent,RECORD_IMAGE_DATA);
                } else if (resultCode == RESULT_CANCELED) {
                    /*重新录入本班级数据*/
                    currentGrade = data.getIntExtra("currentGrade",0);
                    currentRoom = data.getIntExtra("currentRoom",1);
                    /*显示数据*/
                    for (int i = 1;i <= 54;i++){
                        if ( (currentGrade == classArrayGrade[i]) && (currentRoom == classArrayRoom[i]) ) number = i;
                    }
                    switch (currentGrade){
                        case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
                        case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
                        case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
                        case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
                        case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
                        case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
                    }
                    /*更新图片和按钮*/
                    classImage = new File(getImagePath(currentGrade,currentRoom));
                    if (classImage.exists()) {
                        showImage(ShowMode.SHOW_IMAGE);
                        takePhotoButtonMode = DELETE;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    } else {
                        showImage(ShowMode.SHOW_BLANK);
                        takePhotoButtonMode = CAMERA;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                    if (number == 1) lastClassButton.setClickable(false); //设置“上一个”按钮不可用
                    if (number < max) nextClassButton.setImageResource(android.R.drawable.ic_media_ff);//将按钮设置为“下一个”按钮
                }
                break;
            case RECORD_IMAGE_DATA:
                if (resultCode == RESULT_OK){
                    /*读取第一个班级并显示*/
                    number = 1;
                    currentGrade = classArrayGrade[number];
                    currentRoom = classArrayRoom[number];
                    switch (currentGrade){
                        case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
                        case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
                        case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
                        case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
                        case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
                        case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
                    }
                    /*初始化图片和按钮*/
                    showImage(ShowMode.SHOW_BLANK);
                    takePhotoButtonMode = CAMERA;
                    takePhotoButton.setImageResource(android.R.drawable.ic_menu_camera);
                    nextClassButton.setImageResource(android.R.drawable.ic_media_ff);
                    checkNewVersion();
                }
                break;
            case SET_REGULATION:
                if (resultCode == RESULT_OK){
                    /*读取班级顺序并储存*/
                    classArrayGrade = new int[55];
                    for (int j = 1;j <= 54;j++) classArrayGrade[j] = 0;//初始化classArrayGrade[]
                    classArrayRoom = new int[55];
                    for (int j = 1;j <= 54;j++) classArrayRoom[j] = 0;//初始化classArrayRoom[]
                    max = 0;
                    for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
                        for (classroom = 1;classroom <= 18;classroom++){
                            number = pref.getInt(grade+""+classroom,0);
                            if (number != 0){
                                classArrayGrade[number] = grade;
                                classArrayRoom[number] = classroom;
                                if (max<number) max = number;
                            }
                        }
                    }
                    /*强制设置检查顺序*/
                    if (max == 0) {
                        Toast.makeText(MainActivity.this,"请先设置检查顺序",Toast.LENGTH_SHORT);
                        Intent intent = new Intent(mApplication.getContext(),SetRegulationActivity.class);
                        intent.putExtra("pattern",pattern);
                        startActivityForResult(intent,SET_REGULATION);
                    }
                    /*读取第一个班级并显示*/
                    number = 1;
                    currentGrade = classArrayGrade[number];
                    currentRoom = classArrayRoom[number];
                    switch (currentGrade){
                        case SENIOR_1:classNameView.setText("高一（"+currentRoom+"）班");break;
                        case SENIOR_2:classNameView.setText("高二（"+currentRoom+"）班");break;
                        case SENIOR_3:classNameView.setText("高三（"+currentRoom+"）班");break;
                        case JUNIOR_1:classNameView.setText("初一（"+currentRoom+"）班");break;
                        case JUNIOR_2:classNameView.setText("初二（"+currentRoom+"）班");break;
                        case JUNIOR_3:classNameView.setText("初三（"+currentRoom+"）班");break;
                    }
                    /*初始化按钮*/
                    nextClassButton.setImageResource(android.R.drawable.ic_media_ff);
                }
                break;
            default:
                break;
        }
    }

    private void showImage(ShowMode mode){
        switch (mode){
            case SHOW_IMAGE:
                Glide.with(MainActivity.this).load(classImage)
                        .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(photoImage);
                break;
            case SHOW_BLANK:
                photoImage.setImageResource(R.color.avoscloud_feedback_input_wrap_background);
                break;
            default:
                break;
        }
    }

    private String getImagePath(int grade,int classroom){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SD卡已装入
            return getExternalFilesDir("images").getPath()+grade+""+classroom+".jpg";
        } else {
            //SD卡未装入
            return getFilesDir().getPath()+grade+""+classroom+".jpg";
        }
    }

    /*申请读写权限*/
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void checkNewVersion(){
        /*检查更新*/
        AVQuery<AVObject> query = new AVQuery<>("Updates");
        query.whereExists("file");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                if (list != null){
                    final int version = list.get(0).getInt("version");
                    if (version > mApplication.version){
                        final AVObject currentList = list.get(0);
                        final TextView progressText = (TextView) findViewById(R.id.progress_text);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("检查更新")
                                .setMessage("有新版本更新，是否更新？")
                                .setCancelable(true);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this,"正在下载...",Toast.LENGTH_SHORT);
                                AVFile avfile = currentList.getAVFile("file");
                                avfile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, AVException e) {
                                        if (e == null) {
                                            String directory = Environment
                                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                                    .getPath();
                                            File file = new File(directory + "/smartmonitor"+version+".apk");
                                            FileOutputStream stream = null;
                                            try {
                                                file.createNewFile();
                                                stream = new FileOutputStream(file);
                                                stream.write(bytes);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            } finally {
                                                try {
                                                    stream.close();
                                                        /*安装软件*/
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
                                                        Uri apkUri = FileProvider.getUriForFile(MainActivity.this
                                                                , "com.shawpoo.app.fileprovider", file);
                                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                    } else {
                                                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                                    }
                                                    startActivity(intent);
                                                    Log.d("stream", "success");
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "下载错误:" + e, Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                    }
                                }, new ProgressCallback() {
                                    @Override
                                    public void done(Integer integer) {
                                        progressText.setText("已下载"+integer+"%");
                                    }
                                });
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (currentList.getBoolean("significant")){
                                    Toast.makeText(MainActivity.this,"重要更新，请更新后再使用",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        }).show();
                    }
                }
            }
        });
    }
}
