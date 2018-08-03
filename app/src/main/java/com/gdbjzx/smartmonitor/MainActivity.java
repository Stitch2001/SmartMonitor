package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN = 1;

    private static final int TAKE_PHOTO = 2;

    private DrawerLayout mDrawerLayout;

    private NavigationView navigationView;

    private ImageView photoImage;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        photoImage = (ImageView) findViewById(R.id.photo_image);

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

        /*设置拍照按钮*/
        FloatingActionButton takeFhotoButton = (FloatingActionButton) findViewById(R.id.take_photo);
        takeFhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getExternalCacheDir(),"1.jpg");//此处照片名称后续需要修改
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e){
                    e.printStackTrace();
                }//检测是否存在图片，存在则删除
                if (Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(MainActivity.this,"com.gdbjzx.smartmonitor",outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }//获取图片的本地真实路径
                /*启动相机*/
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
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
                    try {
                        //显示拍摄的照片
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        photoImage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                        Snackbar.make(photoImage,"图片未找到",Snackbar.LENGTH_SHORT);
                    }
                }
                break;
            default:
        }
    }
}
