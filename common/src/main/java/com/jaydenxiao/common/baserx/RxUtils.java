package com.jaydenxiao.common.baserx;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by yue on 17/10/9
 * rxjava的点击时间限制重复点击
 * 
 */
public class RxUtils {
    public static final int WINDOW_DURATION = 1000;        //时间
    @CheckResult
    @NonNull
    public static Observable<Object> clicks(@NonNull View view) {
        return RxView.clicks(view)
                .throttleFirst(WINDOW_DURATION, TimeUnit.MILLISECONDS);
    }

    @CheckResult
    @NonNull
    public static <T extends Adapter> Observable<Integer> itemClicks(@NonNull AdapterView view) {
        return RxAdapterView.itemClicks(view)
                .throttleFirst(WINDOW_DURATION, TimeUnit.MILLISECONDS);
    }

}
