package com.gdbjzx.smartmonitor;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

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

        /*配置版本号*/
        version = 6;

        /*Android7.0以上版本打开相机闪退修复*/
        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        /*操作数据库的配置*/
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"kJ4C4D7mWjjAD2X5G3JpPe81-gzGzoHsz","MwsllyERC65LKHtrq2qE2ifL");
        //AVOSCloud.setDebugLogEnabled(true);//发布时删除
    }

    public static Context getContext(){
        return context;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

}
