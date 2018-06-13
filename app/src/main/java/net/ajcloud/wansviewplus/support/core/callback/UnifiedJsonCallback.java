package net.ajcloud.wansviewplus.support.core.callback;

import net.ajcloud.wansviewplus.support.core.okgo.model.Response;

import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;

/**
 * 统一处理返回
 * Created
 */

public class UnifiedJsonCallback<T extends ResponseBean, R> extends JsonCallback<T> {
    private OkgoCommonListener<R> listener;
    public UnifiedJsonCallback(OkgoCommonListener<R> listener) {
        this.listener = listener;
    }
    @Override
    public void onSuccess(Response<T> response) {
        ResponseBean<R> responseBean = response.body();
        if (responseBean.isSuccess()) {
            listener.onSuccess(responseBean.result);
        } else {
            switch (responseBean.getResultCode()) {
                case 9100: // 超时
                    break;
                case 9101: //被踢
                    break;
                default:
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
            }
        }
    }

    @Override
    public void onError(Response<T> response) {
        super.onError(response);
        listener.onFail(-1, MainApplication.getApplication().getString(net.ajcloud.wansviewplus.R.string.Service_Error));
    }
}
