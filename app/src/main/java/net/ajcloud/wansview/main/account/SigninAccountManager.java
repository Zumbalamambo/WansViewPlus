package net.ajcloud.wansview.main.account;

import android.content.Context;
import android.text.TextUtils;

import net.ajcloud.wansview.entity.SigninAccountInfo;
import net.ajcloud.wansview.entity.SigninAccountInfoDao;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.support.core.bean.SigninBean;
import net.ajcloud.wansview.support.core.cipher.CipherUtil;

/**
 * Created by mamengchao on 2018/05/30.
 * Function:登录账号信息管理类
 */
public class SigninAccountManager {

    private Context context;
    private SigninAccountInfoDao signinAccountInfoDao;

    public SigninAccountManager(Context context) {
        this.context = context;
        signinAccountInfoDao = MainApplication.getApplication().getDaoSession().getSigninAccountInfoDao();
    }

    private static SigninAccountInfoDao getDao() {
        return MainApplication.getApplication().getDaoSession().getSigninAccountInfoDao();
    }

    /**
     * 保存当前登录账号
     */
    public void saveCurrentAccount(String mail, String password, SigninBean bean) {
        //清除最近登陆账号信息
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount != null) {
            recentLoginAccount.setIsRecent(false);
            recentLoginAccount.setAccessToken(null);
            recentLoginAccount.setRefreshToken(null);
            recentLoginAccount.setExpiresIn(null);
            recentLoginAccount.setTokenType(null);
            recentLoginAccount.setScope(null);
            signinAccountInfoDao.update(recentLoginAccount);
        }

        SigninAccountInfo info = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.Mail.eq(mail))
                .unique();

        if (info == null) {
            String salt = CipherUtil.getRandomSalt();
            String encodePwd = CipherUtil.naclEncodeLocal(password, salt);
            String encodeAccess = CipherUtil.naclEncodeLocal(bean.accessToken, salt);
            String encodeRefresh = CipherUtil.naclEncodeLocal(bean.refreshToken, salt);
            bean.accessToken = encodeAccess;
            bean.refreshToken = encodeRefresh;
            info = new SigninAccountInfo(mail, encodePwd, salt, bean);
            signinAccountInfoDao.insert(info);
        } else {
            String salt = info.getSalt();
            String encodePwd = CipherUtil.naclEncodeLocal(password, salt);
            String encodeAccess = CipherUtil.naclEncodeLocal(bean.accessToken, salt);
            String encodeRefresh = CipherUtil.naclEncodeLocal(bean.refreshToken, salt);
            info.setIsRecent(true);
            info.setAccessToken(encodeAccess);
            info.setRefreshToken(encodeRefresh);
            info.setExpiresIn(bean.expiresIn);
            info.setTokenType(bean.tokenType);
            info.setScope(bean.tokenType);
            info.setPassword(encodePwd);
            info.setSalt(salt);
            signinAccountInfoDao.update(info);
        }
    }

    public String getCurrentAccountMail() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : recentLoginAccount.getMail();
    }

    public String getCurrentAccountAccessToken() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : CipherUtil.naclDecodeLocal(recentLoginAccount.getAccessToken(), recentLoginAccount.getSalt());
    }

    public String getCurrentAccountRefreshToken() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : CipherUtil.naclDecodeLocal(recentLoginAccount.getRefreshToken(), recentLoginAccount.getSalt());
    }

    public String getCurrentAccountSalt() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : recentLoginAccount.getSalt();
    }

    public String getCurrentAccountPassword() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : CipherUtil.naclDecodeLocal(recentLoginAccount.getPassword(), recentLoginAccount.getSalt());
    }

    public String getCurrentAccountGesture() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        return recentLoginAccount == null ? null : CipherUtil.naclDecodeLocal(recentLoginAccount.getGesture(), recentLoginAccount.getSalt());
    }

    public void setCurrentAccountGesture(String gesture) {
        SigninAccountInfo currentAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        String salt = currentAccount.getSalt();
        String gesturePwd = CipherUtil.naclEncodeLocal(gesture, salt);
        currentAccount.setGesture(gesturePwd);
        signinAccountInfoDao.update(currentAccount);
    }

    /**
     * 清除登录信息，登出时用
     */
    public void clearAccountSigninInfo() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount != null) {
            recentLoginAccount.setAccessToken(null);
            recentLoginAccount.setRefreshToken(null);
            recentLoginAccount.setExpiresIn(null);
            recentLoginAccount.setTokenType(null);
            recentLoginAccount.setScope(null);
            signinAccountInfoDao.update(recentLoginAccount);
        }
    }

    /**
     * 判断是否自动登录
     */
    public boolean isAutoSignin() {
        SigninAccountInfo recentLoginAccount = signinAccountInfoDao.queryBuilder()
                .where(SigninAccountInfoDao.Properties.IsRecent.eq(true))
                .unique();
        if (recentLoginAccount == null) {
            return false;
        } else {
            if (TextUtils.isEmpty(recentLoginAccount.getAccessToken())) {
                return false;
            } else {
                return true;
            }
        }
    }
}
