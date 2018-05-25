package net.ajcloud.wansview.support.core.callback;

import net.ajcloud.wansview.support.core.bean.ResponseBean;
import net.ajcloud.wansview.support.core.okhttp.callback.AbsCallback;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * Created by mamengchao on 2018/05/21.
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        //第二层数据类型
        Type rawType = ((ParameterizedType) type).getRawType();
        //第二层数据的泛型类型
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];

        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(body.charStream());
        if (typeArgument == Object.class) {
            ResponseBean bean = gson.fromJson(jsonReader, type);
            response.close();
            return (T) bean;
        } else if (rawType == ResponseBean.class) {
            ResponseBean bean = gson.fromJson(jsonReader, type);
            response.close();
            return (T) bean;
        } else {
            response.close();
            throw new IllegalStateException("基类错误无法解析!");
        }
    }
}
