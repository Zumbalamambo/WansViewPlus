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
import net.ajcloud.wansview.main.application.MainApplication;

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
        view.findViewById(net.ajcloud.wansview.R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               new UserApiUnit(getActivity()).changePassword("805901025@qq.com", oldPwd.getText().toString(), newPwd.getText().toString(), new UserApiUnit.UserApiCommonListener<SigninBean>() {
//                   @Override
//                   public void onSuccess(SigninBean bean) {
//
//                   }
//
//                   @Override
//                   public void onFail(int code, String msg) {
//
//                   }
//               });
            }
        });
        view.findViewById(net.ajcloud.wansview.R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getApplication().logout();
//                new UserApiUnit(getActivity()).signout(new UserApiUnit.UserApiCommonListener<Object>() {
//                    @Override
//                    public void onSuccess(Object bean) {
//
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
