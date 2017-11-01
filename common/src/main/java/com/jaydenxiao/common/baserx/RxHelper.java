package com.jaydenxiao.common.baserx;

import com.jaydenxiao.common.basebean.BaseRespose;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;


/**
 * des:对服务器返回数据成功和失败处理
 */

public class RxHelper {

    /** 对服务器返回数据进行预处理 **/
    public static <T> ObservableTransformer<BaseRespose<T>, T> handleResult() {
        return upstream -> {
            return upstream.flatMap(new Function<BaseRespose<T>, ObservableSource<T>>() {
                @Override
                public ObservableSource<T> apply(BaseRespose<T> result) throws Exception {
                    if (result != null) {
                        if (result.success()) {
                            return createData(result.result);
                        } else {
                            return Observable.error(new ServerException(result.errmsg));
                        }
                    }
                    return Observable.error(new ServerException("请求失败"));
                }
            }).compose(RxSchedulersThread.io_main()); //调度
        };
    }

    /** 创建成功的数据 **/
    private static <T> Observable<T> createData(final T data) {
        return Observable.create(e -> {
            try {
                e.onNext(data);
                e.onComplete();
            }catch (Exception ex){
                e.onError(ex);
            }
           
        });
    }

}
