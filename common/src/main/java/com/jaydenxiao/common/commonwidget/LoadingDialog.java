package com.jaydenxiao.common.commonwidget;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jaydenxiao.common.R;
import com.jaydenxiao.common.loading_dialog.AVLoadingIndicatorView;
import com.jaydenxiao.common.loading_dialog.BallSpinFadeLoaderIndicator;

/**
 * description:弹窗浮动加载进度条
 * Created by xsf
 * on 2016.07.17:22
 */
public class LoadingDialog {

    /** 加载数据对话框 */
    private static Dialog mLoadingDialog;
    private static  AVLoadingIndicatorView indicatorview;

    /**
     * 显示加载对话框
     * @param activity 上下文
     * @param msg 对话框显示内容
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading(Activity activity, String msg, boolean cancelable) {
        if (mLoadingDialog == null) {
            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null, false);
            indicatorview = (AVLoadingIndicatorView) view.findViewById(R.id.indicator);
            indicatorview.setIndicator(new BallSpinFadeLoaderIndicator());
            indicatorview.show();
            mLoadingDialog = new Dialog(activity, R.style.CustomProgressDialog);
            mLoadingDialog.setCancelable(cancelable);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            mLoadingDialog.show();
        }
        return mLoadingDialog;
    }

    public static Dialog showDialogForLoading(Activity activity) {
        if (mLoadingDialog == null) {
            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null, false);
            indicatorview = (AVLoadingIndicatorView) view.findViewById(R.id.indicator);
            indicatorview.setIndicator(new BallSpinFadeLoaderIndicator());
            indicatorview.show();
            mLoadingDialog = new Dialog(activity, R.style.CustomProgressDialog);
            mLoadingDialog.setCancelable(true);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            mLoadingDialog.show();
        }
        return mLoadingDialog;
    }

    /** 关闭加载对话框 **/
    public static void cancelDialogForLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog.dismiss();
            if (indicatorview != null) indicatorview.hide();

            mLoadingDialog = null;
            indicatorview = null;
        }
    }

}
