package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }
        navigationView.setCheckedItem(R.id.nav_set_regulation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_login:
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    case R.id.nav_set_regulation:

                    default:
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
            default:
        }
        return true;
    }
}
