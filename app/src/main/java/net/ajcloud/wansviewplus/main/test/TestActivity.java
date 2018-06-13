package net.ajcloud.wansviewplus.main.test;

import android.view.View;
import android.widget.EditText;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.cipher.CipherUtil;
import net.ajcloud.wansviewplus.support.tools.WLog;

public class TestActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Test");
        getToolbar().setLeftImg(R.mipmap.icon_back);
    }

    @Override
    protected void initData() {
        final EditText oldPwd = findViewById(R.id.oldPwd);
        final EditText newPwd = findViewById(R.id.newPwd);
        String text = "805901025@qq.com";
        final String salt = CipherUtil.getRandomSalt();
        final String password = CipherUtil.naclEncodeLocal(text, salt);
        WLog.d("localnacl", "salt:" + salt);
        WLog.d("localnacl", "password:" + password);
        oldPwd.setText(password);
        findViewById(net.ajcloud.wansviewplus.R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninAccountManager.getInstance().setCurrentAccountGesture("1236987");


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new UserApiUnit(TestActivity.this).refreshToken();
                    }
                }).start();

//                new DeviceApiUnit(this).unBind("K03868WPCGRPFDX4",  new OkgoCommonListener<Object>() {
//                    @Override
//                    public void onSuccess(Object bean) {
//                        ToastUtil.single("ok");
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//                        ToastUtil.single(msg);
//                    }
//                });
//                new DeviceApiUnit(this).unBind("K03868KVLJNASXNC",  new OkgoCommonListener<Object>() {
//                    @Override
//                    public void onSuccess(Object bean) {
//                        ToastUtil.single("ok");
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//                        ToastUtil.single(msg);
//                    }
//                });
//                new DeviceApiUnit(this).unBind("K038682CY5NV1PI9",  new OkgoCommonListener<Object>() {
//                    @Override
//                    public void onSuccess(Object bean) {
//                        ToastUtil.single("ok");
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//                        ToastUtil.single(msg);
//                    }
//                });
            }
        });
        findViewById(net.ajcloud.wansviewplus.R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new UserApiUnit(this).signout(new OkgoCommonListener<Object>() {
//                    @Override
//                    public void onSuccess(Object bean) {
//                        SigninTwiceActivity.start(this, SigninAccountManager.getInstance().getCurrentAccountMail());
//                        this.finish();
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//
//                    }
//                });
            }
        });
    }
}
