package net.ajcloud.wansview.support.customview.camera;

import android.content.Context;
import android.view.View;


/**
 * Created
 */

public class PTZView {

    private Context context;
    private View view;
    private CloudDirectionLayout directionLayout;
    private View.OnClickListener addAngleListener;
    private View.OnClickListener navigationListener;
    private CloudDirectionLayout.OnSteerListener ptzListener;

    public PTZView(Context context, View.OnClickListener addAngleListener, View.OnClickListener navigationListener, CloudDirectionLayout.OnSteerListener ptzListener) {
        this.context = context;
        this.addAngleListener = addAngleListener;
        this.navigationListener = navigationListener;
        this.ptzListener = ptzListener;
    }

    public View getView() {
        view = View.inflate(context, net.ajcloud.wansview.R.layout.view_ptz, null);
        view.findViewById(net.ajcloud.wansview.R.id.add_angle).setOnClickListener(addAngleListener);
        view.findViewById(net.ajcloud.wansview.R.id.navigation).setOnClickListener(navigationListener);
        directionLayout = (CloudDirectionLayout) view.findViewById(net.ajcloud.wansview.R.id.ptz_direction_control);
        directionLayout.setListener(ptzListener);
        return view;
    }

}
