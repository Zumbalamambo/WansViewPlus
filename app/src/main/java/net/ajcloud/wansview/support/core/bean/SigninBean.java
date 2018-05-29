package net.ajcloud.wansview.support.core.bean;

/**
 * Created by mamengchao on 2018/05/23.
 * 登录bean
 */
public class SigninBean {
    public String accessToken;
    public String refreshToken;
    public String scope;     //请求范围
    public String tokenType; //token类型
    public String expiresIn; //有效期
}
