package com.jaydenxiao.common.basebean;

/**
 * des:封装服务器返回数据
 * Created by xsf
 * on 2016.09.9:47
 */
public class BaseRespose<T> {

    private String CODE_ERROR_0 = "-100";
    private String CODE_ERROR_1 = "-101";
    private String CODE_ERROR_2 = "-102";

    public String code;
    public String errmsg;
    public String accessToken;
    public String errorPram;
    public T result;

    public boolean success() {
        return "0".equals(code);
    }

    /** Token失效 **/
    public boolean error(){
        return CODE_ERROR_0.equals(code) ||
                CODE_ERROR_1.equals(code) ||
                CODE_ERROR_2.equals(code);
    }

    @Override
    public String toString() {
        return "BaseRespose{" +
                "CODE_ERROR_0='" + CODE_ERROR_0 + '\'' +
                ", CODE_ERROR_1='" + CODE_ERROR_1 + '\'' +
                ", CODE_ERROR_2='" + CODE_ERROR_2 + '\'' +
                ", code='" + code + '\'' +
                ", errmsg='" + errmsg + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", errorPram='" + errorPram + '\'' +
                ", result=" + result +
                '}';
    }
}
