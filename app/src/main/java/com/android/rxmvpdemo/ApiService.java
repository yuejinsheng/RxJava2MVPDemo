package com.android.rxmvpdemo;

import com.jaydenxiao.common.basebean.BaseRespose;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ${yue} on 2017/9/30 0030.
 */

public interface ApiService {

    /**获取轮播广告图片**/
    @GET("getBannerList")
    Observable<BaseRespose<List<Banner>>> getBannerList(@Query("type") String type);
}
