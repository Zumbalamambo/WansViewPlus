package com.ajcloud.wansview.support.utils.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import java.util.Map;

/**
 * Created by mamengchao on 2018/05/14.
 * sharepreference工具类
 */
public class SPUtil {

    private static final String TAG = "SPUtil";
    private static Map<String, SPUtil> spMap = new ArrayMap<>();
    private SharedPreferences mPreferences;

    private SPUtil(Context context, String name) {
        mPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SPUtil getSPUtil(Context context, String name) {
        SPUtil p = spMap.get(name);
        if (p == null) {
            synchronized (SPUtil.class) {
                if (p == null) {
                    p = new SPUtil(context, name);
                    spMap.put(name, p);
                }
            }
        }
        return p;
    }

    /**
     * 通用存储
     */
    public void put(String key, Object object) {
        if (object instanceof String) {
            mPreferences.edit().putString(key, (String) object).apply();
        } else if (object instanceof Integer) {
            mPreferences.edit().putInt(key, (Integer) object).apply();
        } else if (object instanceof Boolean) {
            mPreferences.edit().putBoolean(key, (Boolean) object).apply();
        } else if (object instanceof Float) {
            mPreferences.edit().putFloat(key, (Float) object).apply();
        } else if (object instanceof Long) {
            mPreferences.edit().putLong(key, (Long) object).apply();
        } else {
            mPreferences.edit().putString(key, object.toString()).apply();
        }
    }

    /**
     * 通用获取
     */
    public Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return mPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return mPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return mPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return mPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return mPreferences.getLong(key, (Long) defaultObject);
        } else {
            return mPreferences.getString(key, null);
        }
    }

    public Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

    public Boolean contain(String key) {
        return mPreferences.contains(key);
    }

    public void remove(String key) {
        mPreferences.edit().remove(key).apply();
    }

    public void clear() {
        mPreferences.edit().clear().apply();
    }
}
