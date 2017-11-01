package com.android.rxmvpdemo;

import android.widget.TextView;

import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxHelper;
import com.jaydenxiao.common.baserx.RxSubscriber;
import com.jaydenxiao.common.manager.ServiceManager;

import java.util.List;

public class MainActivity extends BaseActivity<MainView,MainPresenter> implements MainView{

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
         //不需要mvp模式的直接在类中直接创建的
        ServiceManager.create(ApiService.class).getBannerList("1") 
                .compose(RxHelper.<List<Banner>>handleResult())
                .subscribe(new RxSubscriber<List<Banner>>(this,false) {
             @Override
              protected void _onNext(List<Banner> banners) {
                ((TextView)findViewById(R.id.text)).setText(banners.get(0).toString());
              }
                    /**
                     * 这个方法可以重写，也可以不重写，看你子类的需求
                     * @param message
                     */
                    @Override
                    protected void _onError(String message) {
                        super._onError(message);
                        ((TextView)findViewById(R.id.text)).setText(message);
                    }
                });
              
        /**
         * 需要mcp模式调用
         */
        //mPresenter.getBannerList("1");
    }

  

    @Override
    public void showbannerList(List<Banner> list) {
    }
}
