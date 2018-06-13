package net.ajcloud.wansviewplus.main.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.account.SigninTwiceActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.cipher.CipherUtil;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.tools.WLog;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.List;

/**
 * Created by mamengchao on 2018/05/15.
 * 我的
 */
public class MineFragment extends Fragment {

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        toolbar.setOverflowIcon(null);
        toolbarLayout.setTitle("Me");
        toolbarLayout.setExpandedTitleColor(Color.BLACK);
        toolbarLayout.setExpandedTitleGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        toolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);


        final EditText oldPwd = view.findViewById(R.id.oldPwd);
        final EditText newPwd = view.findViewById(R.id.newPwd);
        String text = "805901025@qq.com";
        final String salt = CipherUtil.getRandomSalt();
        final String password = CipherUtil.naclEncodeLocal(text, salt);
        WLog.d("localnacl", "salt:" + salt);
        WLog.d("localnacl", "password:" + password);
        oldPwd.setText(password);
        view.findViewById(net.ajcloud.wansviewplus.R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninAccountManager.getInstance().setCurrentAccountGesture("1236987");

                new DeviceApiUnit(getActivity()).unBind("K03868WPCGRPFDX4",  new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        ToastUtil.single("ok");
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                    }
                });
                new DeviceApiUnit(getActivity()).unBind("K03868KVLJNASXNC",  new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        ToastUtil.single("ok");
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                    }
                });
                new DeviceApiUnit(getActivity()).unBind("K038682CY5NV1PI9",  new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        ToastUtil.single("ok");
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                    }
                });
            }
        });
        view.findViewById(net.ajcloud.wansviewplus.R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserApiUnit(getActivity()).signout(new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        SigninTwiceActivity.start(getContext(), SigninAccountManager.getInstance().getCurrentAccountMail());
                        getActivity().finish();
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
    }

}
