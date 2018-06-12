package net.ajcloud.wansviewplus.entity.camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 消息中心条目
 *
 * @author Sharper
 * @since 2014-07-31
 */
public final class EventMessage implements Serializable {

    private String id;

    /**
     * 用户UID
     */
    private String uid;

    /**
     * 是否推送
     */
    private boolean push;

    /**
     * 推送token
     */
    private List<Pushtoken> pushtokens;

    /**
     * 消息Cate
     */
    private int cate;

    /**
     * 消息Action
     */
    private int action;

    /**
     * 参数
     */
    private Map<String, String> params;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间,缺省值-当时时间
     */
    private long ctime;

    /**
     * 推送时间,缺省值-0L
     */
    private long ptime;

    /**
     * 访问时间,缺省值-0L
     * 是否阅读, 判断此值
     */
    private long atime;

    /**
     * 按时间分类
     */
    private int section;

    /**
     * 是否被选中
     */
    private boolean isSelect;

    public EventMessage() {

        this.id = UUID.randomUUID().toString();
        this.title = "";
        this.content = "";
        this.push = false;
        this.pushtokens = new ArrayList<Pushtoken>();
        this.params = new HashMap<String,String>();
        this.ctime = System.currentTimeMillis();
        this.ptime = 0L;
        this.atime = 0L;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    public List<Pushtoken> getPushtokens() {
        return pushtokens;
    }

    public void setPushtokens(List<Pushtoken> pushtokens) {
        this.pushtokens = pushtokens;
    }

    public int getCate() {
        return cate;
    }

    public void setCate(int cate) {
        this.cate = cate;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public long getPtime() {
        return ptime;
    }

    public void setPtime(long ptime) {
        this.ptime = ptime;
    }

    public long getAtime() {
        return atime;
    }

    public void setAtime(long atime) {
        this.atime = atime;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getHeadTime() {
        String time = null;
        try {
            Date date = new Date(ctime);
            time = date.getHours() + ":00";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public String getHeadDate() {
        String time = null;
        try {
            Date date = new Date(ctime);
            time = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public String getWeekday() {
        String weekday = "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ctime);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                //weekday = ZTEHomecareSDK.getString("zhsdk_http_sunday");
                break;
            case Calendar.MONDAY:
                //weekday = ZTEHomecareSDK.getString("zhsdk_http_monday");
                break;
            case Calendar.TUESDAY:
                //weekday = ZTEHomecareSDK.getString("zhsdk_http_tuesday");
                break;
            case Calendar.WEDNESDAY:
                //weekday = ZTEHomecareSDK.getString("zhsdk_http_wednesday");
                break;
            case Calendar.THURSDAY:
                //weekday = ZTEHomecareSDK.getString("zhsdk_http_thursday");
                break;
            case Calendar.FRIDAY:
                //weekday = ZTEHomecareSDK.getString("zhsdk_http_friday");
                break;
            case Calendar.SATURDAY:
                //weekday = ZTEHomecareSDK.getString("zhsdk_http_saturday");
                break;
        }
        return weekday;
    }

    public String getImageUrl() {
        String imagePath = null;
        if(null != params) {
            imagePath = params.get("imageurl");
            if (null == imagePath && params.containsKey("ext")) {
                String tmp = params.get("ext");
                try {
                    JSONObject jo = new JSONObject(tmp);
                    /*if (EMCate.DETECTION == getCate()) {
                        JSONArray ja = jo.getJSONArray("images");
                        if (ja != null && ja.length() > 0) {
                            imagePath = ja.get(0).toString().trim();
                        }
                    } else if (EMCate.INTERACT == getCate()) {
                        imagePath = jo.getString("imageurl");
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return imagePath;
    }

    public String getThumbUrl() {
        String imagePath = null;
        if(null != params) {
            imagePath = params.get("thumbs");
            if (null == imagePath && params.containsKey("ext")) {
                String tmp = params.get("ext");
                try {
                    JSONObject jo = new JSONObject(tmp);
                    JSONArray ja = jo.getJSONArray("thumbs");
                    if (ja != null && ja.length() > 0) {
                        imagePath = ja.get(0).toString().trim();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return imagePath;
    }

    public String getVideoUrl() {
        String videoPath = null;
        if(null != params) {
            videoPath = params.get("videourl");
            if (null == videoPath && params.containsKey("ext")) {
                String tmp = params.get("ext");
                try {
                    JSONObject jo = new JSONObject(tmp);
                    videoPath = jo.getString("videourl");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return videoPath;
    }

    public String getOdm() {
        if (null != params) {
            return params.get("odm");
        }
        return null;
    }

    public String getOid() {
        if (null != params) {
            return params.get("oid");
        }
        return null;
    }

    /**
     * 获取小时和分钟
     * @return
     */
    public String getHourAndMinTime() {
        String time = null;
        try {
            Date date = new Date(ctime);
            time = String.format("%02d:%02d", date.getHours(), date.getMinutes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }
}
