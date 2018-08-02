package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private int islogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Button loginButton = (Button) findViewById(R.id.login_button);
        final CheckBox rememberPasswordCheck = (CheckBox) findViewById(R.id.remember_password_check);
        final CheckBox autoLoginCheck = (CheckBox) findViewById(R.id.auto_login_check);
        final EditText idText = (EditText) findViewById(R.id.id_text);
        final EditText passwordText = (EditText) findViewById(R.id.password_text);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }//设置Toolbar为默认ActionBar，设置图标
        SharedPreferences sharedPreferences = getSharedPreferences("pw",MODE_PRIVATE);//打开存储文件
        /*读取账号密码*/
        boolean isRemember = sharedPreferences.getBoolean("isRemember",true);
        if (isRemember){
            String id = sharedPreferences.getString("id","");
            String password = sharedPreferences.getString("password","");
            idText.setText(id);
            passwordText.setText(password);
            rememberPasswordCheck.setChecked(true);
        }
        /*读取账号密码*/
        Snackbar.make(loginButton,"请先登录",Snackbar.LENGTH_SHORT).show();
        islogined = RESULT_CANCELED;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                final String idString,passwordString;
                idString = idText.getText().toString();
                passwordString = passwordText.getText().toString();
                if (idString.isEmpty()){
                    Snackbar.make(v,"请输入账号",Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (passwordString.isEmpty()) {
                    Snackbar.make(v,"请输入密码",Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    AVUser.logInInBackground(idString, passwordString, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if (e == null){
                                progressBar.setVisibility(View.INVISIBLE);//收起进度条
                                SharedPreferences.Editor editor = getSharedPreferences("pw",MODE_PRIVATE).edit();//创建存储文件
                                /*记住密码操作*/
                                if (rememberPasswordCheck.isChecked()){
                                    editor.putString("id",idString);
                                    editor.putString("password",passwordString);
                                    editor.putBoolean("isRemember",true);
                                    editor.apply();
                                } else {
                                    editor.putString("id","");
                                    editor.putString("password","");
                                    editor.putBoolean("isRemember",false);
                                    editor.apply();
                                } //明文密码，后续版本需要加密
                                /*记住密码操作*/
                                /*自动登录标记操作*/
                                if (autoLoginCheck.isChecked()){
                                    editor.putBoolean("isAuto",true);
                                    editor.apply();
                                } else {
                                    editor.putBoolean("isAuto",false);
                                    editor.apply();
                                }
                                /*自动登录标记操作*/
                                /*返回主界面操作*/
                                Intent intent = new Intent();
                                islogined = RESULT_OK;
                                setResult(islogined,intent);
                                finish();
                                /*返回*/
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                switch (e.getCode()){
                                    case 0:
                                        Snackbar.make(idText,"无网络连接",Snackbar.LENGTH_SHORT).show();
                                        break;
                                    case 210:
                                        Snackbar.make(idText,"用户名或密码错误",Snackbar.LENGTH_SHORT).show();
                                        passwordText.setText("");
                                        break;
                                    case 211:
                                        Snackbar.make(idText,"找不到用户",Snackbar.LENGTH_SHORT).show();
                                        passwordText.setText("");
                                        break;
                                    case 219:
                                        Snackbar.make(idText,"登录次数超过限制，请稍候重试",Snackbar.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Snackbar.make(idText,"未知错误，请与管理员联系，错误代码："+e.getCode(),Snackbar.LENGTH_SHORT).show();
                                        Log.e("LoginActivity",e.getCode()+" "+e.getMessage());
                                }
                            }
                        }
                    });
                }
            }
        });
        autoLoginCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoLoginCheck.isChecked()){
                    rememberPasswordCheck.setChecked(true);
                }
            }
        });
        /*自动登录*/
        boolean isAuto = sharedPreferences.getBoolean("isAuto",false);
        if (isAuto) {
            autoLoginCheck.setChecked(true);
            loginButton.callOnClick();
        }
        /*自动登录*/
    }

}
