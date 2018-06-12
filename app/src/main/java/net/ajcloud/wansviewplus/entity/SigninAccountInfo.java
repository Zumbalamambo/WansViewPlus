package net.ajcloud.wansviewplus.entity;

import net.ajcloud.wansviewplus.support.core.bean.SigninBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by mamengchao on 2018/05/30.
 * Function:登录过的账号信息
 */
@Entity
public class SigninAccountInfo {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    @Unique
    private String mail;
    private String accessToken;
    private String refreshToken; //用于刷新token
    private String scope;     //请求范围
    private String tokenType; //token类型
    private long expiresIn; //有效期
    private boolean isRecent;
    private String password;
    private String gesture;
    private String salt;

    public SigninAccountInfo(@NotNull String mail, String password, String salt, SigninBean bean) {
        this.mail = mail;
        this.accessToken = bean.accessToken;
        this.refreshToken = bean.refreshToken;
        this.scope = bean.scope;
        this.tokenType = bean.tokenType;
        this.expiresIn = System.currentTimeMillis() + bean.expiresIn;
        this.password = password;
        this.salt = salt;
        this.isRecent = true;
    }

    @Generated(hash = 1652198078)
    public SigninAccountInfo(Long id, @NotNull String mail, String accessToken, String refreshToken,
            String scope, String tokenType, long expiresIn, boolean isRecent, String password,
            String gesture, String salt) {
        this.id = id;
        this.mail = mail;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.isRecent = isRecent;
        this.password = password;
        this.gesture = gesture;
        this.salt = salt;
    }

    @Generated(hash = 106869194)
    public SigninAccountInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return this.expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean getIsRecent() {
        return this.isRecent;
    }

    public void setIsRecent(boolean isRecent) {
        this.isRecent = isRecent;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGesture() {
        return this.gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }


}
