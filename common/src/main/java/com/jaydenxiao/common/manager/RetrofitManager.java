package com.jaydenxiao.common.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.commonutils.NetWorkUtils;
import com.jaydenxiao.common.url.BaseConstant;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ClassName: RetrofitManager<p>
 * Fuction: Retrofit请求管理类<p>
 * CreateDate:2016/2/13 20:34<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public enum RetrofitManager {

    INSTANCE;
    //设缓存有效期为两天
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置，头部Cache-Control设为maxd-age=0时则不会使用缓存而请求服务器
    private static final String CACHE_CONTROL_NETWORK = "max-age=0";
    private static final int TIME_OUT = 30;  //超时时间
    private static volatile OkHttpClient mOkHttpClient;
    private static volatile OkHttpClient mOkHttpClient1;

    private Retrofit mRetrofit;
    private Retrofit mRetrofit1;

    
    //有传token的请求进行拦截跳到登陆
    public Retrofit net() {
        if (mRetrofit == null) {
            initOkHttpClient();
            mRetrofit = new Retrofit.Builder().baseUrl(BaseConstant.BASEURL).client(mOkHttpClient)
                    //ExGsonConverterFactory 这个是为了请求返回的参数进行拦截，进行处理
                    .addConverterFactory(ExGsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

     //没有token进行拦截
    public Retrofit net1() {
        if (mRetrofit1 == null) {
            initOkHttpClient();
            mRetrofit1 = new Retrofit.Builder().baseUrl(BaseConstant.BASEURL).client(mOkHttpClient1)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return mRetrofit1;
    }

    // 配置OkHttpClient
    private void initOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                //有公共参数进行加密
                if (mOkHttpClient == null) {
                    //开启Log
                    HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
                    logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    //   LogUtils.e("初始化mOkHttpClient");
                    // 因为BaseUrl不同所以这里Retrofit不为静态，但是OkHttpClient配置是一样的,静态创建一次即可
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(BaseApplication.getAppContext().getCacheDir(), "HttpCache"),
                            1024 * 1024 * 100);
                    OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
 
                    okHttpBuilder
                            .cache(cache)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)//网络读写缓存拦截器
                            //HeaderInterceptor()这个类是为了处理请求带有公共参数，如果你们需要可以用这样的
                            .addInterceptor(new HeaderInterceptor())//添加头部监听(写入公共参数)
                            .addInterceptor(mRewriteCacheControlInterceptor)//添加读写缓存拦截器
                            .addInterceptor(logInterceptor)   //log打印
                            .retryOnConnectionFailure(false)//尝试进行重新连接
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)// 设置连接时间
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)//设置读写时间
                            .writeTimeout(TIME_OUT, TimeUnit.SECONDS);//设置请求超时
                    mOkHttpClient = okHttpBuilder.build();
                }
                //无公共参数进行拦截
                if (mOkHttpClient1 == null) {
                    Cache cache = new Cache(new File(BaseApplication.getAppContext().getCacheDir(), "HttpCache"),
                            1024 * 1024 * 100);
                    OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

                    okHttpBuilder.cache(cache)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)//网络读写缓存拦截器
                            .addInterceptor(mRewriteCacheControlInterceptor)//添加读写缓存拦截器
                            .addInterceptor(mLoggingInterceptor)// 设置请求消息拦截器
                            .retryOnConnectionFailure(true)//尝试进行重新连接
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)// 设置连接时间
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)//设置读写时间
                            .writeTimeout(TIME_OUT, TimeUnit.SECONDS);//设置请求超时
//                            .cookieJar(new CookiesManager());//cookie自动管理类
                    mOkHttpClient1 = okHttpBuilder.build();
                }
            }
        }
    }

    

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private Interceptor mRewriteCacheControlInterceptor = chain -> {
        Request request = chain.request();
        String cacheControl = request.cacheControl().toString();
        if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
            request = request.newBuilder()
                    .cacheControl(TextUtils.isEmpty(cacheControl)?CacheControl.FORCE_NETWORK:CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        if (NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                    .removeHeader("Pragma")
                    .build();
        }
    };

    // 打印返回的json数据拦截器
    public Interceptor mLoggingInterceptor = chain -> {
        final Request request = chain.request();

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.

        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(charset);
            } catch (UnsupportedCharsetException e) {
                Log.e("异常","Couldn't decode the response body; charset is likely malformed.");
                return response;
            }
        }

        if (contentLength != 0) {
            Log.v("--","--------------------------------------------开始打印返回数据----------------------------------------------------");
        }
        //检测token失效的情况
//            if (response shows expired token){//根据和服务端的约定判断token过期
//
//                //取出本地的refreshToken
//                String refreshToken = "sssgr122222222";
//
//                // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
//                ApiService service = ServiceManager.getService(ApiService.class);
//                Call<String> call = service.refreshToken(refreshToken);
//
//                //要用retrofit的同步方式
//                String newToken = call.execute().body();
//
//
//                // create a new request and modify it accordingly using the new token
//                Request newRequest = request.newBuilder().header("token", newToken)
//                        .build();
//
//                // retry the request
//
//                originalResponse.body().close();
//                return chain.proceed(newRequest);
//            }
        return response;
    };

    /** 根据网络状况获取缓存的策略 **/
    @NonNull
    public String getCacheControl() {
        return NetWorkUtils.isNetConnected(BaseApplication.getAppContext()) ? CACHE_CONTROL_NETWORK : CACHE_CONTROL_CACHE;
    }

}
