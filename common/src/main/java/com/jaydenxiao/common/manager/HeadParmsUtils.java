package com.jaydenxiao.common.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.commonutils.TokenUtits;
import com.jaydenxiao.common.security.MD5;
import com.jaydenxiao.common.security.RSAUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${yue} on 2017/6/26 0026.
 *   请求网络有关公参和头部的信息
 */
public class HeadParmsUtils {

    private static final String KEY = "32AD3958-8532-4C52-9D91-4D19EC4E474A";
    
    /**
     *  获取公参 
     *  有请求的参数和这里的公参
     **/
    public static Map<String, String> getParms(Map<String,String> hashMap, boolean flag) {
        Map<String,String> map = new HashMap<>();
        map.putAll(hashMap);
        String accessToken = TokenUtits.getToken();
        String apiUid = TokenUtits.getApiUid();
        String channel = getAppMetaData("UMENG_CHANNEL");
        hashMap.put("OS", "Android");
        hashMap.put("version", "1.0.0");
        hashMap.put("pageSize", "10");
        hashMap.put("accessToken", accessToken);
        hashMap.put("apiUid", apiUid);
        hashMap.put("channel", channel);
        transParms(hashMap);//加密参数
        String sign = createLinkString(paraFilter(hashMap)) + KEY;
        sign = MD5.encryption(sign); //把所以的参数在加密
        hashMap.put("sign", sign);
        if(flag) {
            if (map.size() > 0) {
                for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                    hashMap.remove(stringStringEntry.getKey());
                }
            }
        }
        return hashMap;
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值或者异常)，则返回值为空
     */
    public static String getAppMetaData(String key) {
        if (TextUtils.isEmpty(key)) return "";
        String resultData = "";
        try {
            Context appContext = BaseApplication.getAppContext();
            PackageManager packageManager = appContext.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                        appContext.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null && applicationInfo.metaData != null)
                    resultData = applicationInfo.metaData.get(key) + "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            resultData="";
        }
        return resultData;
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                sb.append(key).append("=").append(value) ;
            } else {
                sb.append(key).append("=").append(value).append("&") ;
            }
        }
        return sb.toString();
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    
    private static final String[] fields = {"mobile", "password", "realName",
            "identifyCard", "userName", "payPassword", "cardNo", "newPayPassword"};
    
    /** 加密参数 **/
    public static void transParms(Map<String,String> hashMap){
        for (String field : fields) {
            String content = hashMap.get(field);
            if (!TextUtils.isEmpty(content)) {
                hashMap.put(field, RSAUtils.getRsaString(content));
            }
        }
    }

}
