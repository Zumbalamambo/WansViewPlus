package net.ajcloud.wansviewplus.main.application;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by mamengchao on 2018/07/12.
 * Function:
 */
public abstract class WVFragment extends Fragment {

    protected FragmentActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        this.mActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}
