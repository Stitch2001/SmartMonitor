package com.gdbjzx.smartmonitor;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by Administrator on 2018/7/19.
 */

public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"kJ4C4D7mWjjAD2X5G3JpPe81-gzGzoHsz","MwsllyERC65LKHtrq2qE2ifL");
        AVOSCloud.setDebugLogEnabled(true);//发布时删除
    }
}
