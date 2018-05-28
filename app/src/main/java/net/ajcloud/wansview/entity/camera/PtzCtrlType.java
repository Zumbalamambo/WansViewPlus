package net.ajcloud.wansview.entity.camera;

/**
 * Created by smilence on 2014/6/24.
 */
public class PtzCtrlType {
    /**
     * 停止
     */
    public final static int STOP = 0;

    /**
     * 向上转，不带停止指令
     */
    public final static int TOP_NO_STOP = 3;

    /**
     * 向下转，不带停止指令
     */
    public final static int BOTTOM_NO_STOP = 4;

    /**
     * 右转，不带停止指令
     */
    public final static int RIGHT_NO_STOP = 2;

    /**
     * 左转，不带停止指令
     */
    public final static int LEFT_NO_STOP = 1;

    /**
     * 水平巡航
     */
    public final static int CRUISE_HORIZONTAL = 6;

    /**
     * 竖直巡航
     */
    public final static int CRUISE_VERTICAL = 7;

    /**
     * 向左滑动
     */
    public final static int LEFT_WITH_STOP = 8;

    /**
     * 向右滑动
     */
    public final static int RIGHT_WITH_STOP = 9;

    /**
     * 向上滑动
     */
    public final static int TOP_WITH_STOP = 10;

    /**
     * 向下滑动
     */
    public final static int BOTTOM_WITH_STOP = 11;
}
