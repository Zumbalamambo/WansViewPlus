package net.ajcloud.wansviewplus.main.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.BuildConfig;
import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.account.SigninTwiceActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.history.LocalHistoryActivity;
import net.ajcloud.wansviewplus.main.mine.security.SecurityActivity;
import net.ajcloud.wansviewplus.main.test.TestActivity;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.customview.dialog.LogoutDialog;
import net.ajcloud.wansviewplus.support.customview.dialog.ProgressDialogManager;
import net.ajcloud.wansviewplus.support.tools.TimeLock;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import static net.ajcloud.wansviewplus.main.history.LocalHistoryActivity.ITEM_IMAGE;
import static net.ajcloud.wansviewplus.main.history.LocalHistoryActivity.ITEM_VIDEO;

/**
 * Created by mamengchao on 2018/05/15.
 * 我的
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private static String LOGOUT = "LOGOUT";
    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;

    private SwipeRefreshLayout refreshLayout;
    private TextView accountTextView;
    private TextView countTextView;
    private LinearLayout videoLayout;
    private LinearLayout photoLayout;
    private RelativeLayout storageLayout;
    private RelativeLayout securityLayout;
    private RelativeLayout versionLayout;
    private TextView versionTextView;
    private ImageView updateImage;
    private RelativeLayout aboutLayout;
    private RelativeLayout logoutLayout;
    private RelativeLayout testLayout;

    private LogoutDialog logoutDialog;
    private TimeLock timeLock = new TimeLock();

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

        refreshLayout = view.findViewById(R.id.refresh_layout);
        accountTextView = view.findViewById(R.id.tv_account);
        countTextView = view.findViewById(R.id.tv_count);
        videoLayout = view.findViewById(R.id.ll_video);
        photoLayout = view.findViewById(R.id.ll_photo);
        storageLayout = view.findViewById(R.id.rl_message);
        securityLayout = view.findViewById(R.id.rl_security);
        versionLayout = view.findViewById(R.id.rl_version);
        versionTextView = view.findViewById(R.id.tv_version_name);
        updateImage = view.findViewById(R.id.iv_version);
        aboutLayout = view.findViewById(R.id.rl_about);
        logoutLayout = view.findViewById(R.id.rl_logout);
        testLayout = view.findViewById(R.id.rl_test);
        if (BuildConfig.DEBUG) {
            testLayout.setVisibility(View.VISIBLE);
        } else {
            testLayout.setVisibility(View.GONE);
        }
        logoutDialog = new LogoutDialog(getActivity());
        initListener();
        initData();
    }

    private void initData() {
        String account = SigninAccountManager.getInstance().getCurrentAccountMail();
        int count = MainApplication.getApplication().getDeviceCache().getCounts();
        String versionName = BuildConfig.VERSION_NAME;
        accountTextView.setText(account);
        countTextView.setText(String.format(getResources().getString(R.string.me_device_count), count + ""));
        versionTextView.setText("V" + versionName);
        refreshLayout.setRefreshing(false);
    }

    private void initListener() {
        videoLayout.setOnClickListener(this);
        photoLayout.setOnClickListener(this);
        storageLayout.setOnClickListener(this);
        securityLayout.setOnClickListener(this);
        versionLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        logoutLayout.setOnClickListener(this);
        testLayout.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        logoutDialog.setDialogClickListener(new LogoutDialog.OnDialogClickListener() {
            @Override
            public void logout() {
                doLogout();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (timeLock.isLock()) {
            return;
        }
        timeLock.lock();

        switch (v.getId()) {
            case R.id.ll_video:
                LocalHistoryActivity.start(getActivity(), ITEM_VIDEO);
                break;
            case R.id.ll_photo:
                LocalHistoryActivity.start(getActivity(), ITEM_IMAGE);
                break;
            case R.id.rl_message:
                break;
            case R.id.rl_security:
                startActivity(new Intent(getActivity(), SecurityActivity.class));
                break;
            case R.id.rl_version:
                break;
            case R.id.rl_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.rl_logout:
                if (!logoutDialog.isShowing()) {
                    logoutDialog.show();
                }
                break;
            case R.id.rl_test:
                startActivity(new Intent(getActivity(), TestActivity.class));
                break;
            default:
                break;
        }
    }

    private void doLogout() {
        ProgressDialogManager.getDialogManager().showDialog(LOGOUT, getActivity());
        new UserApiUnit(getActivity()).signout(new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(LOGOUT, 0);
                SigninTwiceActivity.start(getActivity(), SigninAccountManager.getInstance().getCurrentAccountMail());
                getActivity().finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(LOGOUT, 0);
                ToastUtil.single(msg);
            }
        });
    }
}
