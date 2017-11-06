package com.jaydenxiao.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.jaydenxiao.common.R;
import com.jaydenxiao.common.baseapp.AppManager;
import com.jaydenxiao.common.commonutils.TUtil;
import com.jaydenxiao.common.commonutils.ToastUitl;
import com.jaydenxiao.common.commonwidget.LoadingDialog;
import com.jaydenxiao.common.commonwidget.StatusBarCompat;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity<V extends BaseView,T extends BasePresenter<V>>extends RxAppCompatActivity implements BaseView {

    public T mPresenter;
    protected Activity mActivity;
    public Context mContext;
    private boolean isConfigChange = false;

    protected TextView toolbar_header;
    protected Toolbar toolbar;
    public boolean isNeedConfirmAppExit = false;

    //ButterKnife
    private Unbinder mUnbinder;
    public long mExitTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isConfigChange = false;
        doBeforeSetcontentView();
        setContentView(getLayoutId());

        mUnbinder = ButterKnife.bind(this);
        mActivity = this;
        mContext = getBaseContext();
        mPresenter = TUtil.getT(this, 1);
        mPresenter.attachView((V) this);
        if (mPresenter != null) mPresenter.mContext = this;
        handIntent();
        initView();
    }

    /** 设置layout前配置 **/
    private void doBeforeSetcontentView() {
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
        SetStatusBarColor();
    }

    /*********************子类实现*****************************/
    //获取布局文件
    public abstract int getLayoutId();

    public void handIntent(){}

    //初始化view
    public abstract void initView();


    //初始化toolbar
    protected void initToolbar(int title, boolean showNavigationIcon, boolean menuItemClick,
                               OnMenuClickListener onMenuClickListener) {
        initToolbar(this.getResources().getString(title), showNavigationIcon, menuItemClick, onMenuClickListener);
    }

    //初始化toolbar
    @SuppressWarnings("ConstantConditions")
    protected void initToolbar(String title, boolean showNavigationIcon,
                               boolean menuItemClick, final OnMenuClickListener onMenuClickListener) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_header = (TextView) findViewById(R.id.toolbar_title_tv);
        toolbar_header.setText(title);
        
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (showNavigationIcon) {
            toolbar.setNavigationIcon(R.drawable.icon_back);
            // Menu item click 的監聽事件一樣要設定在 setSupportActionBar 才有作用
            // toolbar.setOnMenuItemClickListener(onMenuItemClick);
            //设置toolbar右侧的点击事件
            toolbar.setNavigationOnClickListener(v -> {
                if(listener != null) listener.onNavigationClick();
                finish();
            });
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

    //点击返回需要处理的东西
    OnNavigationOnClickListener listener;
    public interface  OnNavigationOnClickListener{
        void onNavigationClick();
    }

     public void setOnNavigationOnClickListener(OnNavigationOnClickListener listener){
         this.listener=listener;
     }

    /** 着色状态栏（4.4以上系统有效） **/
    protected void SetStatusBarColor() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorAccent));
    }

    /** 着色状态栏（4.4以上系统有效） **/
    protected void SetStatusBarColor(int color) {
        StatusBarCompat.setStatusBarColor(this, color);
    }

    /** 沉浸状态栏（4.4以上系统有效） **/
    protected void SetTranslanteBar() {
        StatusBarCompat.translucentStatusBar(this);
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
        intent.setClass(this, cls);
        if (bundle != null) intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /** 含有Bundle通过Class跳转界面 **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
    }


    /** 短暂显示Toast提示(来自String) **/
    public void showShortToast(String text) {
        ToastUitl.showShort(text);
    }

    /** 短暂显示Toast提示(id) **/
    public void showShortToast(int resId) {
        ToastUitl.showShort(resId);
    }
    
    public void setNeedConfirmAppExit(boolean needConfirmAppExit) {
        isNeedConfirmAppExit = needConfirmAppExit;
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isNeedConfirmAppExit) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                    return false;
                } else {
                    AppManager.getAppManager().AppExit(this, true);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //debug版本不统计crash
           /* if (!BuildConfig.LOG_DEBUG) {
                //友盟统计
                MobclickAgent.onResume(this);
            }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //debug版本不统计crash
     /*   if (!BuildConfig.LOG_DEBUG) {
            //友盟统计
            MobclickAgent.onPause(this);
        }*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isConfigChange = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isConfigChange) AppManager.getAppManager().finishActivity(this);
        if (mUnbinder != null) mUnbinder.unbind();
        if (mPresenter!=null)
            mPresenter.detachView();
    }

}
