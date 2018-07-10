package net.ajcloud.wansviewplus.entity;

/**
 * Created by mamengchao on 2018/07/02.
 * Function:消息中心inbox
 */
public class MessageInfo {
    private String tittle;
    private String message;
    private String date;
    private String img;

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
