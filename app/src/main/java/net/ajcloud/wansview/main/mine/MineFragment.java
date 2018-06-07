package net.ajcloud.wansview.main.mine;

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

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.account.SigninAccountManager;
import net.ajcloud.wansview.main.account.SigninTwiceActivity;
import net.ajcloud.wansview.support.core.api.OkgoCommonListener;
import net.ajcloud.wansview.support.core.api.UserApiUnit;
import net.ajcloud.wansview.support.core.cipher.CipherUtil;
import net.ajcloud.wansview.support.tools.WLog;

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
        view.findViewById(net.ajcloud.wansview.R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPwd.setText(CipherUtil.naclDecodeLocal(password, CipherUtil.getRandomSalt()));
                SigninAccountManager signinAccountManager = new SigninAccountManager(getActivity());
                signinAccountManager.setCurrentAccountGesture("1236987");
            }
        });
        view.findViewById(net.ajcloud.wansview.R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserApiUnit(getActivity()).signout(new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        SigninAccountManager signinAccountManager = new SigninAccountManager(getActivity());
                        SigninTwiceActivity.start(getContext(), signinAccountManager.getCurrentAccountMail());
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
