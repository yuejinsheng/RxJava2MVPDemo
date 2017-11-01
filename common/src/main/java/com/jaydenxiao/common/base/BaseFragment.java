package com.jaydenxiao.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaydenxiao.common.R;
import com.jaydenxiao.common.commonutils.TUtil;
import com.jaydenxiao.common.commonutils.ToastUitl;
import com.jaydenxiao.common.commonwidget.LoadingDialog;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * des:基类fragment
 * Created by xsf
 * on 2016.07.12:38
 */

public abstract class BaseFragment<V extends BaseView,T extends BasePresenter<V>> extends RxFragment implements BaseView {

    protected View rootView;
    public T mPresenter;
    //ButterKnife
    private Unbinder mUnbinder;
    //为了处理懒加载
    public boolean isVisible;
    public boolean isPrepared;//初始化完成  

    protected TextView toolbar_header;
    protected Toolbar toolbar;

    protected Activity mActivity;
    protected Context mContext;

   
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
        mContext = mActivity.getBaseContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutResource(),null);
            mUnbinder = ButterKnife.bind(this, rootView);
            mPresenter = TUtil.getT(this, 1);
            mPresenter.attachView((V) this);
            if (mPresenter != null) mPresenter.mContext = getActivity();
            isPrepared = true;
            initView();
        }
        return rootView;
    }

    //获取布局文件
    protected abstract int getLayoutResource();


    //初始化view
    protected abstract void initView();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            initView();
        } else {
            isVisible = false;
        }
    }
    
    /** 通过Class跳转界面 **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /** 通过Class跳转界面 **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /** 含有Bundle通过Class跳转界面 **/
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /** 含有Bundle通过Class跳转界面 **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
    }

/*    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return bindToLifecycle();
    }   */

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return bindToLifecycle();
    }

    /** 开启加载进度条 **/
    public void startProgressDialog() {
        LoadingDialog.showDialogForLoading(mActivity);
    }

    /** 开启加载进度条 **/
    public void startProgressDialog(String msg) {
        LoadingDialog.showDialogForLoading(mActivity, msg, true);
    }

    /** 停止加载进度条 **/
    public void stopProgressDialog() {
        LoadingDialog.cancelDialogForLoading();
    }

    /** 短暂显示Toast提示(来自String) **/
    public void showShortToast(String text) {
        ToastUitl.showShort(text);
    }

    /** 短暂显示Toast提示(id) **/
    public void showShortToast(int resId) {
        ToastUitl.showShort(resId);
    }

    /** 网络访问错误提醒 **/
    public void showNetErrorTip() {
        ToastUitl.showToastWithImg(getText(R.string.net_error).toString(),R.drawable.ic_wifi_off);
    }

    public void showNetErrorTip(String error) {
        ToastUitl.showToastWithImg(error,R.drawable.ic_wifi_off);
    }

    //初始化toolbar
    protected void initToolbar(int title, boolean showNavigationIcon, boolean menuItemClick,
                               int menuLayout, final OnMenuClickListener onMenuClickListener) {
        initToolbar(this.getResources().getString(title), showNavigationIcon,
                menuItemClick, onMenuClickListener);
        toolbar.inflateMenu(menuLayout);
    }

    //初始化toolbar
    @SuppressWarnings("ConstantConditions")
    protected void initToolbar(String title, boolean showNavigationIcon, boolean menuItemClick,
                               final OnMenuClickListener onMenuClickListener) {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar_header = (TextView) rootView.findViewById(R.id.toolbar_title_tv);
        toolbar_header.setText(title);
        setHasOptionsMenu(true);
        ((BaseActivity) mActivity).setSupportActionBar(toolbar);
        ActionBar actionBar = ((BaseActivity)mActivity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        if (showNavigationIcon) {
            toolbar.setNavigationIcon(R.drawable.icon_back);
            // Menu item click 的監聽事件一樣要設定在 setSupportActionBar 才有作用
            // toolbar.setOnMenuItemClickListener(onMenuItemClick);
            //设置toolbar右侧的点击事件
            toolbar.setNavigationOnClickListener(v -> mActivity.finish());
        }
        if (menuItemClick) {
            toolbar.setOnMenuItemClickListener(item -> {
                onMenuClickListener.onItemClick(item);
                return false;
            });
        }
    }

    //点击事件的回调
    public interface OnMenuClickListener {
        void onItemClick(MenuItem item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mUnbinder != null) mUnbinder.unbind();
        if (mPresenter!=null)
            mPresenter.detachView();
    }

}
