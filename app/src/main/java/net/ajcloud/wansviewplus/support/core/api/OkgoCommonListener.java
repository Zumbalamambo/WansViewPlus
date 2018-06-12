package net.ajcloud.wansviewplus.support.core.api;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    okgo请求通用回调
 */
public interface OkgoCommonListener<T> {
    void onSuccess(T bean);

    void onFail(int code, String msg);

    //custom
}
