package com.jaydenxiao.common.baserx;

import android.app.Activity;
import android.content.Context;
import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.jaydenxiao.common.R;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.commonutils.NetWorkUtils;
import com.jaydenxiao.common.commonutils.ToastUitl;
import com.jaydenxiao.common.commonwidget.LoadingDialog;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;


/**
 * des:订阅封装
 * Created by xsf
 * on 2016.09.10:16
 */
public abstract class RxSubscriber<T> extends DisposableObserver<T> {

    private Context mContext;
    private String msg;
    private boolean showDialog = true;

    /** 是否显示浮动dialog **/
    public void showDialog() {
        this.showDialog = true;
    }
    public void hideDialog() {
        this.showDialog = true;
    }

    public  RxSubscriber(Context context, String msg, boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog = showDialog;
    }

    public RxSubscriber(Context context) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading), true);
    }

    public RxSubscriber(Context context, boolean showDialog) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading), showDialog);
    }

        

    @Override
    public void onComplete() {
         //关掉对话框
        if (showDialog) LoadingDialog.cancelDialogForLoading();
    }


    @Override
    protected void onStart() {
        super.onStart();
        
        //打开对话框
        if (showDialog) {
            try {
                LoadingDialog.showDialogForLoading((Activity) mContext, msg, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        if (showDialog) LoadingDialog.cancelDialogForLoading();
        e.printStackTrace();
        //网络
        if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
            _onError(BaseApplication.getAppContext().getString(R.string.no_net));
        } else if (e instanceof ServerException||e instanceof HttpException) {  //服务器异常
            _onError(e.getMessage());

        } else if (e instanceof SocketTimeoutException || e instanceof ConnectException){   //服务器超时
            _onError(BaseApplication.getAppContext().getString(R.string.time_out));
        }else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            _onError("解析错误");
        }
        }

    protected abstract void _onNext(T t);

    protected  void _onError(String message){
        ToastUitl.showLong(message);
    }

}
