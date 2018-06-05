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
import net.ajcloud.wansview.support.core.socket.CableConnectionUnit;

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
//                byte[] b = new byte[]{
//                  1,9,2,46,1,6,8,46,1,46,1,32,32,32
//                };
//                newPwd.setText(DigitalUtils.bytetoString(b));
            }
        });
        view.findViewById(net.ajcloud.wansview.R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getApplication().logout();
            }
        });
    }

}
