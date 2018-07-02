package net.ajcloud.wansviewplus.main.account;

import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.api.ApiConstant;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;

public class TermsActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_h5;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle("Terms of Use");
            toolbar.setLeftImg(net.ajcloud.wansviewplus.R.mipmap.ic_back);
            toolbar.setRightText("Agree");
            toolbar.setRightTextColor(getResources().getColor(net.ajcloud.wansviewplus.R.color.colorPrimary));
        }
        mWebView = findViewById(R.id.webView);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        String url = ApiConstant.URL_PRIVACY;
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    };

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                finish();
                break;
            default:
                break;
        }
    }
}
