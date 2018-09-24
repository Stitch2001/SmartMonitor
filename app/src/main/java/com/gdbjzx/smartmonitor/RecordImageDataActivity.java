package com.gdbjzx.smartmonitor;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    private static final int PATTERN_NOON = 0;
    private static final int PATTERN_NIGHT = 1;

    private ImageView photoImage;
    private TextView classNameView;
    private FloatingActionButton lastClassButton;
    private FloatingActionButton nextClassButton;
    private TextInputEditText ought;
    private TextInputEditText fact;
    private TextInputEditText leave;
    private TextInputEditText temporary;
    private ProgressBar progressBar;

    private int grade = 1,classroom = 1,currentGrade = 1,currentRoom = 1,number = 1,max = 1,
            oughtNum,factNum,leaveNum,temporaryNum,pattern;
    private int[] classArrayGrade = new int[55];
    private int[] classArrayRoom = new int[55];
    private AVException e = null;
    private File classImage;
    private SharedPreferences pref;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPLOAD_OK:
                    progressBar.setProgress(100);
                    progressBar.setVisibility(View.GONE);
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

        /*判断是设置午休顺序还是晚修顺序*/
        pattern = getIntent().getIntExtra("pattern",-1);
        if (pattern == PATTERN_NOON) {
            pref = getSharedPreferences("RegulationNoonData",MODE_PRIVATE);
            toolbar.setTitle("设置午休检查顺序");
        }
        else{
            pref = getSharedPreferences("RegulationNightData",MODE_PRIVATE);
            toolbar.setTitle("设置晚修检查顺序");
        }

        /*读取班级顺序并储存*/
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
                    saveData(pref);//保存应到实到数据
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

        /*设置在输入完临休数据后自动跳转到下一个班级*/
        temporary.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    nextClassButton.callOnClick();
                }
                return true;
            }
        });
    }

    private boolean saveData(final SharedPreferences pref){
        String oughtString = ought.getText().toString();
        String factString = fact.getText().toString();
        String leaveString = leave.getText().toString();
        String temporaryString = temporary.getText().toString();

        oughtNum = 0;factNum = 0;leaveNum = 0;temporaryNum = 0;
        if (!oughtString.isEmpty()) oughtNum = Integer.parseInt(oughtString);
        if (!factString.isEmpty()) factNum = Integer.parseInt(factString);
        if (!leaveString.isEmpty()) leaveNum = Integer.parseInt(leaveString);
        if (!temporaryString.isEmpty()) temporaryNum = Integer.parseInt(temporaryString);
        final int absent = oughtNum + temporaryNum - factNum - leaveNum;
        if (oughtString.isEmpty() || factString.isEmpty() || leaveString.isEmpty() || temporaryString.isEmpty()){
            return false;
        } else {
            if (absent > 0){
                Toast.makeText(RecordImageDataActivity.this,"经计算，有"+absent+"人缺勤，将给此班级扣"+absent+"分，需要修改请返回"
                        ,Toast.LENGTH_LONG).show();
                /*保存应到实到信息*/
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("ought"+number,oughtNum)
                        .putInt("fact"+number,factNum)
                        .putInt("leave"+number,leaveNum)
                        .putInt("temporary"+number,temporaryNum)
                        .putInt("absent"+number,absent)
                        .apply();
                return true;
            } else if (absent < 0){
                Toast.makeText(RecordImageDataActivity.this,"输入有误，请检查",Toast.LENGTH_SHORT).show();
                return false;
            } else {
                /*保存应到实到信息*/
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("ought"+number,oughtNum)
                        .putInt("fact"+number,factNum)
                        .putInt("leave"+number,leaveNum)
                        .putInt("temporary"+number,temporaryNum)
                        .putInt("absent"+number,absent)
                        .apply();
                return true;
            }
        }
    }

    private boolean uploadData(final SharedPreferences pref){
        progressBar.setVisibility(View.VISIBLE);
        /*开始提交数据*/
        final Handler updateDataHandler = new Handler(){
            public void handleMessage(Message msg){
                progressBar.setProgress(msg.arg1);
                Log.d("msg.arg1",msg.arg1+"");
            }
        };
        new Thread(new Runnable() {
            Runnable updateProgressThread;
            @Override
            public void run() {
                Message message = new Message();
                ArrayList<AVObject> classDataList = new ArrayList<>();
                ArrayList<AVObject> situationDataList = new ArrayList<>();
                ArrayList<AVFile> classImageList = new ArrayList<>();
                for (number = 1;number<=max;number++){
                    /*录入应到实到数据*/
                    AVObject classData = new AVObject("ClassData");
                    classData.put("grade",classArrayGrade[number]);
                    classData.put("classroom",classArrayRoom[number]);
                    classData.put("ought",pref.getInt("ought"+number,0));
                    classData.put("fact",pref.getInt("fact"+number,0));
                    classData.put("leave",pref.getInt("leave"+number,0));
                    classData.put("temporary",pref.getInt("temporary"+number,0));
                    classData.put("absent",pref.getInt("absent"+number,0));
                    classData.put("checker", AVUser.getCurrentUser().getUsername());
                    classDataList.add(classData);
                    /*录入图片*/
                    classImage = new File(getExternalCacheDir(),classArrayGrade[number]+""+classArrayRoom[number]+".jpg");
                    if (classImage.exists()){
                        try {
                            File compressedImage = new Compressor(RecordImageDataActivity.this)
                                    .setMaxHeight(150).setQuality(23).compressToFile(classImage);
                            String fileName = "";
                            switch (classArrayGrade[number]){
                                case SENIOR_1:fileName = "高一（"+classArrayRoom[number]+"）班";break;
                                case SENIOR_2:fileName = "高二（"+classArrayRoom[number]+"）班";break;
                                case SENIOR_3:fileName = "高三（"+classArrayRoom[number]+"）班";break;
                                case JUNIOR_1:fileName = "初一（"+classArrayRoom[number]+"）班";break;
                                case JUNIOR_2:fileName = "初二（"+classArrayRoom[number]+"）班";break;
                                case JUNIOR_3:fileName = "初三（"+classArrayRoom[number]+"）班";break;
                            }
                            AVFile avFile = AVFile.withFile(fileName,compressedImage);
                            classImageList.add(avFile);
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    /*录入扣分情况*/
                    for (int i = 1;i <= pref.getInt("listNum"+number,0);i++){
                        AVObject situationData = new AVObject("SituationData");
                        situationData.put("grade",classArrayGrade[number]);
                        situationData.put("classroom",classArrayRoom[number]);
                        situationData.put("location",pref.getString("location"+number+""+i,""));
                        situationData.put("event",pref.getString("event"+number+""+i,""));
                        situationData.put("score",pref.getInt("score"+number+""+i,0));
                        situationData.put("date",pref.getString("date"+number+""+i,""));
                        situationData.put("checker",AVUser.getCurrentUser().getUsername());
                        situationDataList.add(situationData);
                    }
                }
                /*上传数据*/
                try {
                    AVObject.saveAll(classDataList);
                    AVObject.saveAll(situationDataList);
                    for (AVFile avFile:classImageList) avFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            updateDataHandler.removeCallbacks(updateProgressThread);
                            if (e != null) RecordImageDataActivity.this.e = e;
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(final Integer integer) {
                            updateProgressThread = new Runnable() {
                                @Override
                                public void run() {
                                    Message message = updateDataHandler.obtainMessage();
                                    message.arg1 = integer;
                                    updateDataHandler.sendMessage(message);
                                }
                            };
                            updateDataHandler.post(updateProgressThread);
                            Log.d("progress",integer+"");
                        }
                    });
                } catch (AVException e){
                    e.printStackTrace();
                    RecordImageDataActivity.this.e = e;
                }
                if (RecordImageDataActivity.this.e == null){
                    /*清除扣分数据*/
                    pref.edit().clear().apply();
                    /*清除内存中图片*/
                    for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
                        for (classroom = 1;classroom <= 18;classroom++){
                            classImage = new File(getExternalCacheDir(),grade+""+classroom+".jpg");
                            if (classImage.exists()) classImage.delete();
                        }
                    }
                    /*清除非正常关闭标志*/
                    SharedPreferences.Editor editor = RecordImageDataActivity.this.pref.edit();
                    editor.putBoolean("isError",false).apply();
                    /*发送检查完成消息*/
                    message.what = UPLOAD_OK;
                    handler.sendMessage(message);
                } else {
                    message.what = UPLOAD_FAILED;
                    handler.sendMessage(message);
                }
            }
        }).start();
        return true;
    }

}
