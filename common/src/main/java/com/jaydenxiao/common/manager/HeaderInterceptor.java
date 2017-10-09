package com.jaydenxiao.common.manager;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ${yue} on 2017/9/30 0030.\
 *  头部请求进行拦截并写入公共参数
 */

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = null;
        String method = original.method();
        HttpUrl url = original.url();
        //加密的参数
        Map<String,String> params = new HashMap<>();
        //get请求
        if(method.equals("GET")){
            for (int i = 0; i < url.querySize(); i++) {
                params.put(url.queryParameterName(i), url.queryParameterValue(i));
            }
            //加密后的参数
            HeadParmsUtils.getParms(params,true);
            HttpUrl.Builder builder = url.newBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            request = original.newBuilder().addHeader("Connection", "close").url(builder.build()).build();
        } else  if(method.equals("POST")) {
            if (original.body() instanceof FormBody) {
                FormBody.Builder newFormBoy = new FormBody.Builder();
                FormBody oldFormBody = (FormBody) original.body();
                for (int i=0;i<oldFormBody.size();i++){
                    params.put(oldFormBody.encodedName(i),
                            URLDecoder.decode(oldFormBody.encodedValue(i),"UTF-8"));
                }
                //加密后的参数
                HeadParmsUtils.getParms(params,false);
                //添加加密参数
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    newFormBoy.add(entry.getKey(), entry.getValue());
                }
                request = original.newBuilder().addHeader("Connection", "close")
                        .method(original.method(), newFormBoy.build()).build();
            }
        }
        return chain.proceed(request);
    }
}
