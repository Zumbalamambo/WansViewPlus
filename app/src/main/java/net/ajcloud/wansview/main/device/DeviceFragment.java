package net.ajcloud.wansview.main.device;

import android.content.Intent;
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

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.device.addDevice.AddDeviceSelectActivity;

/**
 * Created by mamengchao on 2018/05/15.
 * 设备页
 */
public class DeviceFragment extends Fragment implements View.OnClickListener {
    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        toolbar.setOverflowIcon(null);
        toolbarLayout.setTitle("Devices");
        toolbarLayout.setExpandedTitleColor(Color.BLACK);
        toolbarLayout.setExpandedTitleGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        toolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_img:
                startActivity(new Intent(getContext(), AddDeviceSelectActivity.class));
                break;
            default:
                break;
        }
    }
}
