package com.gdbjzx.smartmonitor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN = 1;
    private static final int TAKE_PHOTO = 2;

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

    private enum ShowMode {SHOW_IMAGE,SHOW_BLANK};

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageView photoImage;
    private TextView classNameView;
    private FloatingActionButton takePhotoButton;
    private Uri imageUri;

    private int grade,classroom,currentGrade,currentRoom,number,max,takePhotoButtonMode = 1;
    private int[] classArrayGrade = new int[55];
    private int[] classArrayRoom = new int[55];
    private File classImage;

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
                    classImage = new File(getExternalCacheDir(),number+".jpg");
                    showImage(ShowMode.SHOW_IMAGE);
                    takePhotoButtonMode = DELETE;
                    takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        photoImage = (ImageView) findViewById(R.id.photo_image);
        classNameView = (TextView) findViewById(R.id.class_name);

        /*读取班级顺序并储存*/
        final SharedPreferences pref = getSharedPreferences("RegulationData",MODE_PRIVATE);
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
                            pref.getInt("CurrentNumber",number);
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
                    for (int number = 1;number<=18;number++){
                        classImage = new File(getExternalCacheDir(),number+".jpg");
                        if (classImage.exists()) classImage.delete();
                    }
                    SharedPreferences.Editor editor = getSharedPreferences("RegulationData",MODE_PRIVATE).edit();
                    editor.putBoolean("isError",false).apply();
                }
            });
            dialog.show();
        }

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
                        intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivityForResult(intent,LOGIN);
                        break;
                    case R.id.nav_set_regulation:
                        intent = new Intent(MainActivity.this,SetRegulationActivity.class);
                        startActivity(intent);
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

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
                    classImage = new File(getExternalCacheDir(),number+".jpg");
                    try {
                        if (classImage.exists()){
                            classImage.delete();
                        }
                        classImage.createNewFile();
                    } catch (IOException e){
                        e.printStackTrace();
                    }//检测是否存在图片，存在则删除
                    if (Build.VERSION.SDK_INT >= 24){
                        imageUri = FileProvider.getUriForFile(MainActivity.this,"com.gdbjzx.smartmonitor",classImage);
                    } else {
                        imageUri = Uri.fromFile(classImage);
                    }//获取图片的本地真实路径
                    /*启动相机*/
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);
                } else if (takePhotoButtonMode == DELETE){
                    /*清除照片*/
                    new File(getExternalCacheDir(),number+".jpg").delete();
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
                /*待添加*/
            }
        });

        /*设置上一个/下一个班级按钮*/
        FloatingActionButton lastClassButton = (FloatingActionButton) findViewById(R.id.last_class);
        lastClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    classImage = new File(getExternalCacheDir(),number+".jpg");
                    if (classImage.exists()) {
                        showImage(ShowMode.SHOW_IMAGE);
                        takePhotoButtonMode = DELETE;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    } else {
                        showImage(ShowMode.SHOW_BLANK);
                        takePhotoButtonMode = CAMERA;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                }
            }
        });
        FloatingActionButton nextClassButton = (FloatingActionButton) findViewById(R.id.next_class);
        nextClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    classImage = new File(getExternalCacheDir(),number+".jpg");
                    if (classImage.exists()) {
                        showImage(ShowMode.SHOW_IMAGE);
                        takePhotoButtonMode = DELETE;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    } else {
                        showImage(ShowMode.SHOW_BLANK);
                        takePhotoButtonMode = CAMERA;
                        takePhotoButton.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                }
            }
        });

        /*强制登录*/
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivityForResult(intent,LOGIN);

        /*AVObject testObject = new AVObject("TestObject");
        testObject.put("words","这里是北中纪小检");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.d("saved","success!");
                }
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(R.id.nav_monitor);//设置“检查”高亮
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
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case LOGIN:
                if (resultCode == RESULT_CANCELED) finish();
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    classImage = new File(getExternalCacheDir(),number+".jpg");
                    /*显示拍摄的照片*/
                    showImage(ShowMode.SHOW_IMAGE);
                    /*更新按钮*/
                    takePhotoButtonMode = DELETE;
                    takePhotoButton.setImageResource(android.R.drawable.ic_menu_delete);
                    /*实时保存状态*/
                    SharedPreferences.Editor editor = getSharedPreferences("RegulationData",MODE_PRIVATE).edit();
                    editor.putBoolean("isError",true).putInt("CurrentNumber",number).apply();
                }
                break;
            default:
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
}
