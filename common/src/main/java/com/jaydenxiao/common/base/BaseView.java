package com.jaydenxiao.common.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * des:baseview
 * Created by xsf
 * on 2016.07.11:53
 */
public interface BaseView {

    /** 绑定生命周期 **/
    <T> LifecycleTransformer<T> bindToLife();
    void showError(String e);

}
