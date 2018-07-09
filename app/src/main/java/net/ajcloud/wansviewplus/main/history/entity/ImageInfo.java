package net.ajcloud.wansviewplus.main.history.entity;

import java.io.Serializable;

public class ImageInfo implements Serializable {
    private String imagePath;
    private String headTime;
    private int section;
    private boolean isCheck;
    private String imageName;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getHeadTime() {
        return headTime;
    }

    public void setHeadTime(String headTime) {
        this.headTime = headTime;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
