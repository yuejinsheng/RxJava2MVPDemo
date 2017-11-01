package com.android.rxmvpdemo;

import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.baserx.RxHelper;
import com.jaydenxiao.common.baserx.RxSchedulersThread;
import com.jaydenxiao.common.baserx.RxSubscriber;
import com.jaydenxiao.common.manager.ServiceManager;

import java.util.List;

/**
 * Created by ${yue} on 2017/9/30 0030.
 */

public class MainPresenter extends BasePresenter<MainView> {
    
    public void getBannerList(String type){
        createRxSubscriber(ServiceManager.create(ApiService.class).getBannerList(type)
                        .compose(RxHelper.<List<Banner>>handleResult())
                  .compose(RxSchedulersThread.<List<Banner>>io_main())
                ,
                  
                new RxSubscriber<List<Banner>>(mContext,false) {
            @Override
            protected void _onNext(List<Banner> banners) {
                mView.showbannerList(banners);
            }

                    @Override
                    protected void _onError(String message) {
                    }
                });
        
       
    }
}
