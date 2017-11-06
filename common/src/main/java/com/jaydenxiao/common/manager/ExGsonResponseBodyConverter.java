package com.jaydenxiao.common.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaydenxiao.common.baseapp.AppConfig;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.basebean.BaseRespose;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class ExGsonResponseBodyConverter<T> implements Converter<ResponseBody,T> {

    private Gson gson;
    private Type type;

    ExGsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    /** 进行解析预处理操作 **/
    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        String value = responseBody.string();

        BaseRespose httpResponse = new BaseRespose();
        try {
            JSONObject response = new JSONObject(value);
            int code = response.getInt("code");
            if (code == -100 || code == -101 || code == -102) { //Token过期
                //cls可以是token拦截过期跳转到登陆的页面
                String cls =  "LoginActivity";
                Context context = BaseApplication.getAppContext();
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(context, cls));
                intent.putExtra(AppConfig.MESSAGE, response.getString("errmsg"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return gson.fromJson(value, new TypeToken<BaseRespose<List>>(){}.getType());
            } else if (code == 0) { //请求成功
                httpResponse.result = (response.getString("result"));
                return gson.fromJson(value, type);

            } else {    //各种错误信息
                return gson.fromJson(value, new TypeToken<BaseRespose<List>>(){}.getType());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            return gson.fromJson(value, type);
    }

}
