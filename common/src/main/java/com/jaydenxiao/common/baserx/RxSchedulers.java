package com.jaydenxiao.common.baserx;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava调度管理
 *  主线程和子线程的调度
 */
public class RxSchedulers {

    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
        
    }

}
