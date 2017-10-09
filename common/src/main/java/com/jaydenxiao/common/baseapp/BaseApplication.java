package com.jaydenxiao.common.baseapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.jaydenxiao.common.exception.Cockroach;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * APPLICATION
 */
public class BaseApplication extends MultiDexApplication {

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        //Bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "7de4b44a34", false);
        
        //异常处理
       // initexception();
    }

    public static Context getAppContext() {
        return baseApplication;
    }

    public static Resources getAppResources() {
        return baseApplication.getResources();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /** 分包 **/
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /** 异常处理（全局监听） **/
    public void initexception() {
        Cockroach.install(new Cockroach.ExceptionHandler() {
            // handlerException内部建议手动try{  你的异常处理逻辑  }catch(Throwable e){ } ，以防handlerException内部再次抛出异常，导致循环调用handlerException
            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            throwable.printStackTrace();
                        } catch (Throwable e) {}
                    }
                });
            }
        });
    }
    
}
