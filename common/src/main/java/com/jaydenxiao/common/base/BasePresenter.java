package com.jaydenxiao.common.base;

import android.content.Context;

import com.jaydenxiao.common.baserx.RxSubscriber;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * des:基类presenter
 * Created by xsf
 * on 2016.07.11:55
 */
public abstract class BasePresenter<V extends BaseView>{
    protected V mView;
    //rxJava检查内存泄露
    protected CompositeDisposable mCompositeDisposable;
    protected Context mContext;

   
    public void attachView(V view) {
        mView=view;
      
    }

    public void detachView() {
        mView=null;
        unSubscribe();
    }

    
    /**
     * 绑定subscriber 并且添加mCompositeDisposable进行内存泄露管理
     */
    public  <T> void createRxSubscriber(Observable<T> observable, RxSubscriber<T> subscriber){
           observable.compose(mView.bindToLife())
                  .subscribe(subscriber);
          addSubscribe(subscriber);
    }


    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    protected void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证activity结束时取消所有正在执行的订阅
        }
    }
    
}
