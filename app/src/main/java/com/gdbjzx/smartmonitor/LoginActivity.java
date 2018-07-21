package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private int islogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //View view = (View) findViewById(R.id.switch_class);
            //view.setVisibility(View.INVISIBLE);
        }//设置Toolbar为默认ActionBar，设置图标
        Toast.makeText(LoginActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        Button loginButton = (Button) findViewById(R.id.login_button);
        islogined = RESULT_OK;//为了方便，暂时设置为OK
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(islogined,intent);
                finish();
            }
        });
    }

}
