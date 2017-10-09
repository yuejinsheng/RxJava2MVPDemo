/*
 *  @author Payaml
 *  @version 1.0
 *  @date 17-6-7 下午4:22
 *  Copyright 杭州异人异想网络科技有限公司  异人异想手机网贷理财安卓版软件 All Rights Reserved
 *  未经授权不得进行修改、复制、出售及商业使用
 */

package com.jaydenxiao.common.sharepref;

import android.content.Context;
import android.content.SharedPreferences;

import com.jaydenxiao.common.baseapp.BaseApplication;

/**
 * <公共存储类>
 * <功能详细描述>
 * Created by admin on 2016/9/9.
 */
public class SharePref {

    /** 登录类型 **/
    public static final String IS_LOGIN = "is_login";

    /** access_token **/
    public static final String ACCESS_TOKEN = "accessToken";

    /** api_uid **/
    public static final String API_UID = "api_uid";

    /** 用户信息 **/
    public static final String USER_INFO = "user_info";

    /** 是否为新手 **/
    public static final String IS_NEW = "is_new";

    /** 版本号 **/
    public static final String VERSION = "version";

    public static void saveBoolean(String key, boolean value) {
       
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defvalue) {
        return getSharedPreferences().getBoolean(key, defvalue);
    }
    
    /**
     * Save a string value to the shared preference.
     *
     * @param key   to mark the store value.
     * @param value to saved value.
     */
    public static void saveString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    /**
     * Get the specified value through the key value.
     *
     * @param key to retrieve the value.
     * @return the string value returned.
     */
    public static String getString(String key, String def) {
        return getSharedPreferences().getString(key, def);
    }

    /**
     * Save a integer value to the shared preference.
     *
     * @param key   to mark the store value.
     * @param value to saved value.
     */
    public static void saveInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

    /**
     * Get the specified value through the key value.
     *
     * @param key to retrieve the value.
     * @return the integer value returned.
     */
    public static int getInt(String key, int def) {
        return getSharedPreferences().getInt(key, def);
    }

    /**
     * Save a Long value to the shared preference.
     *
     * @param key   to mark the store value.
     * @param value to saved value.
     */
    public static void saveLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).apply();
    }

    /**
     * Get the specified value through the key value.
     *
     * @param key to retrieve the value.
     * @return the integer value returned.
     */
    public static long getLong(String key, long def) {
        return getSharedPreferences().getLong(key, def);
    }

    /** clear all data through the key value **/
    public static void remove(String key){
        getSharedPreferences().edit().remove(key).apply();
    }

    public static void clear(){
        getSharedPreferences().edit().clear().apply();
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    /** Retrieve the package shared preferences object. **/
     public static SharedPreferences getSharedPreferences() {
        return BaseApplication.getAppContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences(String name){
        return BaseApplication.getAppContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
