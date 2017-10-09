package com.android.rxmvpdemo;

import android.widget.TextView;

import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxHelper;
import com.jaydenxiao.common.baserx.RxSubscriber;
import com.jaydenxiao.common.manager.ServiceManager;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class MainActivity extends BaseActivity<MainView,MainPresenter> implements MainView{

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
         //不需要mvp模式的直接在类中直接创建的
          ServiceManager.create(ApiService.class).getBannerList("1").compose(RxHelper.<List<Banner>>handleResult())
                  .map(new Function<List<Banner>, List<Banner>>() {
                      @Override
                      public List<Banner> apply(@NonNull List<Banner> banners) throws Exception {
                          banners.set(0, new Banner());
                          return banners;
                      }
                  }).subscribe(new RxSubscriber<List<Banner>>(this,false) {
              @Override
              protected void _onNext(List<Banner> banners) {
                  ((TextView)findViewById(R.id.text)).setText(banners.get(0).toString());
              }

              @Override
              protected void _onError(String message) {

              }
          });
        /**
         * 
         */
        //mPresenter.getBannerList("1");
    }

  

    @Override
    public void showbannerList(List<Banner> list) {
    }
}
