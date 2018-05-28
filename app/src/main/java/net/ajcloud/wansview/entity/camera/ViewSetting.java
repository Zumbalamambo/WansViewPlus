package net.ajcloud.wansview.entity.camera;

import java.io.Serializable;

/**
 * 视角设置, 支持2个视角
 * User: 锋
 * Date: 12-11-13
 * Time: 上午12:20
 */
public final class ViewSetting implements Serializable {
    /**
     * 序号: 1,2
     */
    private int seq;

    /**
     * 名称，缺省""
     */
    private String name;

    /**
     * 图片地址
     */
    private String viewurl;

    /**
     * 水平绝对角度:[-180,180]
     */
    private int hangle;

    /**
     * 垂直绝对角度:[-180,180]
     */
    private int wangle;

    /**
     * 更新时间
     */
    private long ts;

    private boolean isSelect;

    public ViewSetting() {
    }

    public ViewSetting(int seq, String name, String viewurl, int hangle, int wangle, long ts) {
        this.seq = seq;
        this.name = name;
        this.viewurl = viewurl;
        this.hangle = hangle;
        this.wangle = wangle;
        this.ts = ts;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewurl() {
        return viewurl;
    }

    public void setViewurl(String viewurl) {
        this.viewurl = viewurl;
    }

    public int getHangle() {
        return hangle;
    }

    public void setHangle(int hangle) {
        this.hangle = hangle;
    }

    public int getWangle() {
        return wangle;
    }

    public void setWangle(int wangle) {
        this.wangle = wangle;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
