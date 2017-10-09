package com.jaydenxiao.common.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 *  作者：gaoyin
 *  电话：18810474975
 *  邮箱：18810474975@163.com
 *  版本号：1.0
 *  类描述：
 *  备注消息：
 *  修改时间：2016/12/12 上午10:19
 **/
public class ExGsonConverterFactory extends Converter.Factory {

    private final Gson gson;

    public  static ExGsonConverterFactory create() {
          return create(new Gson());
    }

    public static ExGsonConverterFactory create(Gson gson) {
        return new ExGsonConverterFactory(gson);
    }

    private ExGsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        Log.e("requestBodyConverter","requestBodyConverter");
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new ExGsonRequestBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Log.e("responseBodyConverter","responseBodyConverter");
        return new ExGsonResponseBodyConverter<>(gson, type);
    }
}
