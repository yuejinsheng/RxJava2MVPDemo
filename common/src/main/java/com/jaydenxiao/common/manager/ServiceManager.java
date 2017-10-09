package com.jaydenxiao.common.manager;

import android.support.v4.util.ArrayMap;

/**
 * Created by xdj on 16/3/14.
 * 接口管理
 */
public class ServiceManager {

    private static final ArrayMap<Class, Object> mServiceMap = new ArrayMap<>();

    /**
     * 创建有关加密参数和拦截跳转的retrofit+okHttp的创建，返回关于retrofit接口观察者对象
     * @param serviceClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> serviceClass) {
        Object service = mServiceMap.get(serviceClass);
        if (service == null) {
            service = RetrofitManager.INSTANCE.net().create(serviceClass);
            mServiceMap.put(serviceClass, service);
        }
        return (T) service;
    }

    /**
     * 没有用到加密的参数和拦截，返回关于retrofit接口观察者对象
     * @param serviceClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T create1(Class<T> serviceClass) {
        Object service = RetrofitManager.INSTANCE.net1().create(serviceClass);
        return (T) service;
    }

}
