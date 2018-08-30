package com.gdbjzx.smartmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class RecordImageDataActivity extends AppCompatActivity {

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private static final int UPLOAD_FAILED = 0;
    private static final int UPLOAD_OK = 1;

    private ImageView photoImage;
    private TextView classNameView;
    private FloatingActionButton lastClassButton;
    private FloatingActionButton nextClassButton;
    private TextInputEditText ought;
    private TextInputEditText fact;
    private TextInputEditText leave;
    private TextInputEditText temporary;
    private ProgressBar progressBar;

    private int grade = 1,classroom = 1,currentGrade = 1,currentRoom = 1,number = 1,max = 1;
    private int[] classArrayGrade = new int[55];
    private int[] classArrayRoom = new int[55];
    private boolean isUploadedclass = false,isUploadedSituations = false,isUploadedImage = false;
    private File classImage;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPLOAD_OK:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RecordImageDataActivity.this,"上传成功",Toast.LENGTH_SHORT);
                    setResult(RESULT_OK);
                    finish();
                    break;
                case UPLOAD_FAILED:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RecordImageDataActivity.this,"上传失败，请检查网络后重试",Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_image_data);
        photoImage = (ImageView) findViewById(R.id.photo_image);
        classNameView = (TextView) findViewById(R.id.class_name);
        ought = (TextInputEditText) findViewById(R.id.ought);
        fact = (TextInputEditText) findViewById(R.id.fact);
        leave = (TextInputEditText) findViewById(R.id.leave);
        temporary = (TextInputEditText) findViewById(R.id.temporary);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ////////////////////////////////////////////////////////////////////////////////////////////
        /*设置Toolbar为默认ActionBar，设置图标*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*读取班级顺序并储存*/
        SharedPreferences pref = getSharedPreferences("RegulationData",MODE_PRIVATE);
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
        lastClassButton = (FloatingActionButton) findViewById(R.id.last_class);
        nextClassButton = (FloatingActionButton) findViewById(R.id.next_class);
        lastClassButton.setClickable(false);
        if (number == max) nextClassButton.setImageResource(R.drawable.tick);
        classImage = new File(getExternalCacheDir(),currentGrade+""+currentRoom+".jpg");
        Glide.with(RecordImageDataActivity.this).load(classImage)
                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(photoImage);
        ought.setText(pref.getString("ought"+number,""));
        fact.setText(pref.getString("fact"+number,""));
        leave.setText(pref.getString("leave"+number,""));
        temporary.setText(pref.getString("temporary"+number,""));

        /*设置上一个/下一个班级按钮*/
        lastClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextClassButton.setImageResource(android.R.drawable.ic_media_ff);
                if (number > 1){//判断前面是否有班级
                    SharedPreferences pref = getSharedPreferences("RecordSituation",MODE_PRIVATE);
                    saveData(pref);//保存应到实到数据
                    /*更新班级名称*/
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
                    classImage = new File(getExternalCacheDir(),currentGrade+""+currentRoom+".jpg");
                    Glide.with(RecordImageDataActivity.this).load(classImage)
                            .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(photoImage);
                    /*更换应到实到数据*/
                    if (pref.getInt("ought"+number,0) == 0) ought.setText("");
                        else ought.setText(pref.getInt("ought"+number,0)+"");
                    if (pref.getInt("fact"+number,0) == 0) fact.setText("");
                        else fact.setText(pref.getInt("fact"+number,0)+"");
                    if (pref.getInt("leave"+number,0) == 0) leave.setText("");
                        else leave.setText(pref.getInt("leave"+number,0)+"");
                    if (pref.getInt("temporary"+number,0) == 0) temporary.setText("");
                        else temporary.setText(pref.getInt("temporary"+number,0)+"");
                    /*光标跳转到“应到”*/
                    ought.requestFocus();
                    ought.setSelection(ought.getText().length());
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
                lastClassButton.setClickable(true);//设置“上一个”按钮可用
                final SharedPreferences pref = getSharedPreferences("RecordSituation",MODE_PRIVATE);
                if (number < max){//判断后面是否有班级
                    if (saveData(pref)){//保存应到实到数据
                        /*更新班级名称*/
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
                        classImage = new File(getExternalCacheDir(),currentGrade+""+currentRoom+".jpg");
                        Glide.with(RecordImageDataActivity.this).load(classImage)
                                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(photoImage);
                        /*更新应到实到数据*/
                        if (pref.getInt("ought"+number,0) == 0) ought.setText("");
                            else ought.setText(pref.getInt("ought"+number,0)+"");
                        if (pref.getInt("fact"+number,0) == 0) fact.setText("");
                            else fact.setText(pref.getInt("fact"+number,0)+"");
                        if (pref.getInt("leave"+number,0) == 0) leave.setText("");
                            else leave.setText(pref.getInt("leave"+number,0)+"");
                        if (pref.getInt("temporary"+number,0) == 0) temporary.setText("");
                            else temporary.setText(pref.getInt("temporary"+number,0)+"");
                        /*光标跳转到“应到”*/
                        ought.requestFocus();
                        ought.setSelection(ought.getText().length());
                    }
                    if (number == max){
                        /*将按钮设置为提交按钮*/
                        nextClassButton.setImageResource(R.drawable.tick);
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RecordImageDataActivity.this)
                            .setTitle("提示")
                            .setMessage("确认提交？")
                            .setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            /*提交到数据库*/
                            uploadData(pref);
                        }
                    });
                    dialog.setNegativeButton("取消",null);
                    dialog.show();
                }
            }
        });
    }

    private boolean saveData(SharedPreferences pref){
        String oughtString = ought.getText().toString();
        String factString = fact.getText().toString();
        String leaveString = leave.getText().toString();
        String temporaryString = temporary.getText().toString();

        int oughtNum = 0 ,factNum = 0,leaveNum = 0,temporaryNum = 0;
        if (!oughtString.isEmpty()) oughtNum = Integer.parseInt(oughtString);
        if (!factString.isEmpty()) factNum = Integer.parseInt(factString);
        if (!leaveString.isEmpty()) leaveNum = Integer.parseInt(leaveString);
        if (!temporaryString.isEmpty()) temporaryNum = Integer.parseInt(temporaryString);

        if (oughtString.isEmpty() || factString.isEmpty() || leaveString.isEmpty() || temporaryString.isEmpty()){
            return false;
        } else {
            /*保存应到实到信息*/
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("ought"+number,oughtNum)
                    .putInt("fact"+number,factNum)
                    .putInt("leave"+number,leaveNum)
                    .putInt("temporary"+number,temporaryNum)
                    .apply();
            return true;
        }
    }

    private boolean uploadData(final SharedPreferences pref){
        progressBar.setVisibility(View.VISIBLE);
        /*开始提交数据*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Message message = new Message();
                final AVObject classData = new AVObject("ClassData");
                AVObject situationData = new AVObject("SituationData");
                for (number = 1;number<=max;number++){
                    /*上传应到实到数据*/
                    classData.put("grade",classArrayGrade[number]);
                    classData.put("classroom",classArrayRoom[number]);
                    classData.put("ought",pref.getInt("leave"+number,0));
                    classData.put("fact",pref.getInt("fact"+number,0));
                    classData.put("leave",pref.getInt("leave"+number,0));
                    classData.put("temporary",pref.getInt("temporary"+number,0));
                    classData.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                if (number == max){
                                    isUploadedclass = true;
                                    if (isUploadedclass && isUploadedSituations && isUploadedImage){
                                        pref.edit().clear();
                                        message.what = UPLOAD_OK;
                                        handler.sendMessage(message);
                                    }
                                }
                            } else {
                                message.what = UPLOAD_FAILED;
                                handler.sendMessage(message);
                            }
                        }
                    });
                    /*上传图片*/
                    classImage = new File(getExternalCacheDir(),classArrayGrade[number]+""+classArrayRoom[number]+".jpg");
                    if (classImage.exists()){
                        try {
                            File compressedImage = new Compressor(RecordImageDataActivity.this).setQuality(30).compressToFile(classImage);
                            AVFile avFile = AVFile.withFile("Grade"+classArrayGrade[number]+"Class"+classArrayRoom[number],
                                    compressedImage);
                            avFile.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null){
                                        if (number == max){
                                            isUploadedImage = true;
                                            if (isUploadedclass && isUploadedSituations && isUploadedImage){
                                                pref.edit().clear();
                                                message.what = UPLOAD_OK;
                                                handler.sendMessage(message);
                                            }
                                        }
                                    } else {
                                        message.what = UPLOAD_FAILED;
                                        handler.sendMessage(message);
                                    }
                                }
                            });
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    /*上传扣分情况*/
                    for (int i = 1;i <= pref.getInt("listNum"+number,0);i++){
                        situationData.put("grade",classArrayGrade[number]);
                        situationData.put("classroom",classArrayRoom[number]);
                        situationData.put("location",pref.getString("location"+number+""+i,""));
                        situationData.put("event",pref.getString("event"+number+""+i,""));
                        situationData.put("score",pref.getInt("score"+number+""+i,0));
                        situationData.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null){
                                    isUploadedSituations = true;
                                    if (isUploadedclass && isUploadedSituations && isUploadedImage){
                                        pref.edit().clear();
                                        message.what = UPLOAD_OK;
                                        handler.sendMessage(message);
                                    }
                                } else {
                                    message.what = UPLOAD_FAILED;
                                    handler.sendMessage(message);
                                }
                            }
                        });
                    }
                }
                message.what = UPLOAD_OK;
                handler.sendMessage(message);
            }
        }).start();
        return true;
    }

}
