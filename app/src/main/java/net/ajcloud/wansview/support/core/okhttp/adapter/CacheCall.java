/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ajcloud.wansview.support.core.okhttp.adapter;

import net.ajcloud.wansview.support.core.okhttp.adapter.Call;
import net.ajcloud.wansview.support.core.okhttp.cache.CacheEntity;
import net.ajcloud.wansview.support.core.okhttp.cache.policy.CachePolicy;
import net.ajcloud.wansview.support.core.okhttp.cache.policy.DefaultCachePolicy;
import net.ajcloud.wansview.support.core.okhttp.cache.policy.FirstCacheRequestPolicy;
import net.ajcloud.wansview.support.core.okhttp.cache.policy.NoCachePolicy;
import net.ajcloud.wansview.support.core.okhttp.cache.policy.NoneCacheRequestPolicy;
import net.ajcloud.wansview.support.core.okhttp.cache.policy.RequestFailedCachePolicy;
import net.ajcloud.wansview.support.core.okhttp.callback.Callback;
import net.ajcloud.wansview.support.core.okhttp.model.Response;
import net.ajcloud.wansview.support.core.okhttp.request.base.Request;
import net.ajcloud.wansview.support.core.okhttp.utils.HttpUtils;

import net.ajcloud.wansview.support.core.okhttp.callback.Callback;
import net.ajcloud.wansview.support.core.okhttp.request.base.Request;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：带缓存的请求
 * 修订历史：
 * ================================================
 */
public class CacheCall<T> implements net.ajcloud.wansview.support.core.okhttp.adapter.Call<T> {

    private CachePolicy<T> policy = null;
    private Request<T, ? extends Request> request;

    public CacheCall(Request<T, ? extends Request> request) {
        this.request = request;
        this.policy = preparePolicy();
    }

    @Override
    public Response<T> execute() {
        CacheEntity<T> cacheEntity = policy.prepareCache();
        return policy.requestSync(cacheEntity);
    }

    @Override
    public void execute(Callback<T> callback) {
        HttpUtils.checkNotNull(callback, "callback == null");

        CacheEntity<T> cacheEntity = policy.prepareCache();
        policy.requestAsync(cacheEntity, callback);
    }

    private CachePolicy<T> preparePolicy() {
        switch (request.getCacheMode()) {
            case DEFAULT:
                policy = new DefaultCachePolicy<>(request);
                break;
            case NO_CACHE:
                policy = new NoCachePolicy<>(request);
                break;
            case IF_NONE_CACHE_REQUEST:
                policy = new NoneCacheRequestPolicy<>(request);
                break;
            case FIRST_CACHE_THEN_REQUEST:
                policy = new FirstCacheRequestPolicy<>(request);
                break;
            case REQUEST_FAILED_READ_CACHE:
                policy = new RequestFailedCachePolicy<>(request);
                break;
        }
        if (request.getCachePolicy() != null) {
            policy = request.getCachePolicy();
        }
        HttpUtils.checkNotNull(policy, "policy == null");
        return policy;
    }

    @Override
    public boolean isExecuted() {
        return policy.isExecuted();
    }

    @Override
    public void cancel() {
        policy.cancel();
    }

    @Override
    public boolean isCanceled() {
        return policy.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Call<T> clone() {
        return new CacheCall<>(request);
    }

    public Request getRequest() {
        return request;
    }
}
