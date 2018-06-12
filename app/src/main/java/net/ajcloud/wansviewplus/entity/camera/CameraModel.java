package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;


/**
 * Created by zte on 2016/5/23.
 */
public class CameraModel implements Serializable {
    CameraFeatures features;
    String type;
    String _id;
    String[] fwversions;

    public CameraFeatures getFeatures() {
        if(features == null){
            features = new CameraFeatures();
        }
        return features;
    }

    public String getType() {
        return type;
    }

    public String get_id() {
        return _id;
    }

    public String[] getFwversions() {
        return fwversions;
    }

}
