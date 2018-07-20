package net.ajcloud.wansviewplus.main.device;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.WVFragment;

/**
 * Created by mamengchao on 2018/07/17.
 * Function:
 */
public class ReplayTFFragment extends WVFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_replay_tf, container, false);
    }

    public void onBack() {
            mActivity.finish();
    }
}
