package com.gdbjzx.smartmonitor;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by Administrator on 2018/8/6.
 */

public class mApplication extends Application {

    private static Context context;

    public static int version;

    @Override
    public void onCreate() {
        super.onCreate();

        /*全局获取Context的配置*/
        context = getApplicationContext();

        /*配置版本序号*/
        version = 1;

        /*操作数据库的配置*/
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"kJ4C4D7mWjjAD2X5G3JpPe81-gzGzoHsz","MwsllyERC65LKHtrq2qE2ifL");
        //AVOSCloud.setDebugLogEnabled(true);//发布时删除
    }

    public static Context getContext(){
        return context;
    }

}
